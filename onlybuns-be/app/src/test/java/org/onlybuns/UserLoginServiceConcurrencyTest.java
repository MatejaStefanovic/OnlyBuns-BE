package org.onlybuns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlybuns.model.User;
import org.onlybuns.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserLoginServiceConcurrencyTest {

    @Autowired
    private UserLoginService userLoginService;

    @Test
    public void testConcurrentUserRegistrationConflict() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        User user1 = new User();
        user1.setUsername("conflictuser");
        user1.setEmail("user1@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setUsername("conflictuser"); // same username — causes conflict
        user2.setEmail("user2@example.com");
        user2.setPassword("password456");

        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Future<Boolean> future1 = executor.submit(() -> {
            System.out.println("Thread 1 starting registration");
            
            readyLatch.countDown();
            startLatch.await();
            try {
                userLoginService.registerUser(user1);
                System.out.println("Thread 1 registration succeeded");
                return true; 
            } catch (Exception e) {
                System.out.println("Thread 1 registration failed");
                return false; 
            }
        });

        Future<Boolean> future2 = executor.submit(() -> {
            System.out.println("Thread 2 starting registration");
            
            readyLatch.countDown();
            startLatch.await();
            try {
                userLoginService.registerUser(user2);
                System.out.println("Thread 2 registration succeeded");
                return true; 
            } catch (Exception e) {
                System.out.println("Thread 2 registration failed");
                return false; 
            }
        });

        readyLatch.await(); 
        startLatch.countDown();
        
        boolean result1 = false;
        boolean result2 = false;
        try {
            result1 = future1.get();
        } catch (ExecutionException e) {
            // Optional: log e.getCause()
            result1 = false;
        }

        try {
            result2 = future2.get();
        } catch (ExecutionException e) {
            // Optional: log e.getCause()
            result2 = false;
        }

        executor.shutdown();

        // Exactly one should fail
        assertTrue(result1 ^ result2, "Expected one registration to fail due to username conflict");
    }
}

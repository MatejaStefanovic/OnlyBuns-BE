package org.onlybuns;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OptimisticTest {

    @Autowired
    private UserService userService;

    @Test
    public void testOptimisticLockingScenario() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> userService.follow("savo", "sanja"));

        Future<?> future2 = executor.submit(() -> {
            try {
                userService.follow("savo", "sanja");
            } catch (OptimisticLockException e) {
                System.out.println("Optimistic lock exception caught as expected: " + e.getMessage());
            }
        });

        executor.shutdown();
    }
}
package org.onlybuns;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServicePessimisticLockTest {

    @Autowired
    private UserService userService;

    @Test
    public void testPessimisticLockingScenario() throws Throwable {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Prvi thread pokreće `follow`
        executor.submit(() -> {
            System.out.println("Startovan Thread 1");
            userService.follow("savo", "sanja"); // Prva transakcija traje duže zbog pesimističkog zaključavanja
        });

        // Drugi thread pokušava da pokrene `follow` posle 150ms
        Future<?> future2 = executor.submit(() -> {
            System.out.println("Startovan Thread 2");
            try {
                Thread.sleep(150); // Pauza pre nego što drugi thread pokuša
                userService.follow("follower", "following"); // Ovo treba da izazove izuzetak
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Provera izuzetka iz drugog threada pomoću assertThrows
        assertThrows(PessimisticLockingFailureException.class, () -> {
            try {
                future2.get(); // Podize ExecutionException ako se pojavi izuzetak u drugom threadu
            } catch (ExecutionException e) {
                throw e.getCause(); // Bacamo pravi uzrok izuzetka
            }
        });

        executor.shutdown();
    }
}

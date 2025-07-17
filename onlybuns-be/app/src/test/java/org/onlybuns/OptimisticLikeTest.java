package org.onlybuns;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlybuns.service.PostService;
import org.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OptimisticLikeTest {

    @Autowired
    private PostService postService;

    @Test
    public void testOptimisticLockingOnAddLike() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> postService.addLike(74, "mima", 1));

        Future<?> future2 = executor.submit(() -> {
            try {
                Thread.sleep(5000); // pusti prvi da zaključa
                postService.addLike(74, "Jelena", 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            try {
                future1.get(); // prvi prolazi
                future2.get(); // ovde treba da pukne
            } catch (ExecutionException e) {
                throw e.getCause(); // propustimo exception iz drugog threada
            }
        });
        executor.shutdown();
    }
}

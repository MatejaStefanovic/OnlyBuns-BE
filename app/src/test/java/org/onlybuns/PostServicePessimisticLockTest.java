package org.onlybuns;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class PostServicePessimisticLockTest {
    @Autowired
    private PostService postService;

     @Test
    public void testPessimisticLockingOnAddLike() throws Throwable {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Pretpostavka: postoji post sa ID 46 i korisnici "Jelena" i "mima"

        executor.submit(() -> {
            System.out.println("Thread 1 start");
            postService.addLike(46, "Jelena", 1); // Ova će zaključati post i spavati
        });

        Future<?> future2 = executor.submit(() -> {
            try {
                Thread.sleep(150); // Dovoljno da se prvi zaključa
                System.out.println("Thread 2 start");
                postService.addLike(46, "mima", 1); // Ova treba da čeka i može da baci exception
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
       /* try {
            future2.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            // Ignoriši samo JpaSystemException koji ima uzrok PSQLException i specifičnu poruku
            if (cause instanceof org.springframework.orm.jpa.JpaSystemException
                    && cause.getCause() != null
                    && cause.getCause().getCause() instanceof org.postgresql.util.PSQLException
                    && cause.getCause().getCause().getMessage().contains("Large Objects may not be used in auto-commit mode")) {
                System.out.println("Ignorisana JpaSystemException zbog LOB auto-commit problema u testu.");
                return; // Ignorišemo ovaj exception i test prolazi
            }

            // Inače očekujemo PessimisticLockingFailureException
            if (!(cause instanceof org.springframework.dao.PessimisticLockingFailureException)) {
                throw cause; // Baci dalje ako nije očekivani exception
            }
        }*/

        executor.shutdown();
    }
}

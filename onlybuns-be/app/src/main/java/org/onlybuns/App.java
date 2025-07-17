package org.onlybuns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.onlybuns.repository.UserRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableCaching

public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}

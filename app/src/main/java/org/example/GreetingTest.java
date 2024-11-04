package org.example; // Ensure this matches your project structure

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingTest {

    @GetMapping("/greet")
    public String greet() {
        return "Hello11";
    }
}

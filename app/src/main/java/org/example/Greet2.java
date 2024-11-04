package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greet2 {
    @GetMapping("/greet2")
    public String greet2() {
        return "Hellooo";
    }
}

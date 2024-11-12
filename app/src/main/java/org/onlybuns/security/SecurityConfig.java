package org.onlybuns.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean for password encoding
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for development environment (Not recommended for production)
        http.csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                // Allow public access to Swagger UI and related resources

                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/user/activate").permitAll()
                        .requestMatchers("/api/user/login").permitAll()

                        .requestMatchers("/api/post").permitAll()
                        .requestMatchers("/api/posts/{postId}/like").permitAll()
                        .requestMatchers("/api/posts/{postId}/comment").permitAll()
                        .requestMatchers("/api/posts/{postId}/delete").permitAll()
                        .requestMatchers("/api/posts/all").permitAll()
                        .requestMatchers("/api/admin/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/swagger-ui.html","/v3/**", "/swagger-ui/**")
                .permitAll()
                // Require authentication for other endpoints
                .anyRequest().authenticated());

        return http.build();
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:8080")  // Update based on Swagger and frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }


    }

package com.clone.config;

import com.clone.entity.User;
import com.clone.entity.Role;
import com.clone.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
    return args -> {

      // Admin
      repo.findByEmail("admin@technosignia.com").orElseGet(() -> {
        User u = new User();
        u.setName("Admin");
        u.setEmail("admin@technosignia.com");
        u.setRole(Role.ADMIN);
        u.setPasswordHash(encoder.encode("Welcome@123"));
        return repo.save(u);
      });

      // Student
      repo.findByEmail("student@gmail.com").orElseGet(() -> {
        User u = new User();
        u.setName("Student");
        u.setEmail("student@gmail.com");
        u.setRole(Role.TECH_STUDENT);
        u.setPasswordHash(encoder.encode("Welcome@123"));
        return repo.save(u);
      });

    };
  }
}

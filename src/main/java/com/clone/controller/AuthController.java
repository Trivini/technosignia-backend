package com.clone.controller;

import com.clone.dto.AuthRequest;
import com.clone.entity.Role;
import com.clone.entity.User;
import com.clone.repository.UserRepository;
import com.clone.repository.TechnosigniaStudentRepository;
import com.clone.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepo;
  private final TechnosigniaStudentRepository studentRepo;
  private final PasswordEncoder encoder;
  private final JwtUtil jwt;

  private static final String TECH_STUDENT_PASSWORD = "Welcome@123";

  public AuthController(
      UserRepository userRepo,
      TechnosigniaStudentRepository studentRepo,
      PasswordEncoder encoder,
      JwtUtil jwt
  ) {
    this.userRepo = userRepo;
    this.studentRepo = studentRepo;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {

    String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
    String password = request.getPassword() == null ? "" : request.getPassword();

    Optional<User> userOpt = userRepo.findByEmail(email);

    // ✅ ADMIN: normal password
    if (userOpt.isPresent() && userOpt.get().getRole() == Role.ADMIN) {
      User admin = userOpt.get();
      if (!encoder.matches(password, admin.getPasswordHash())) {
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
      }
      String token = jwt.generateToken(admin.getEmail(), admin.getRole().name());
      return ResponseEntity.ok(Map.of("token", token, "role", admin.getRole().name()));
    }

    // ✅ TECH_STUDENT: email must exist in technosignia_students table
    boolean allowed = studentRepo.existsByEmail(email);
    if (!allowed) {
      return ResponseEntity.status(401).body(Map.of("message", "Not a Technosignia student"));
    }

    // ✅ same password for all students
    if (!TECH_STUDENT_PASSWORD.equals(password)) {
      return ResponseEntity.status(401).body(Map.of("message", "Invalid student password"));
    }

    // ✅ create user row if missing
    User student = userOpt.orElseGet(() -> {
      User u = new User();
      u.setEmail(email);
      u.setName(email.contains("@") ? email.substring(0, email.indexOf("@")) : email);
      u.setRole(Role.TECH_STUDENT);
      u.setPasswordHash(encoder.encode(TECH_STUDENT_PASSWORD));
      return userRepo.save(u);
    });

    // ✅ ensure role correct
    if (student.getRole() != Role.TECH_STUDENT) {
      return ResponseEntity.status(401).body(Map.of("message", "Invalid role"));
    }

    String token = jwt.generateToken(student.getEmail(), student.getRole().name());
    return ResponseEntity.ok(Map.of("token", token, "role", student.getRole().name()));
  }
}

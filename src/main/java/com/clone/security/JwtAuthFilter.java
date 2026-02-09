package com.clone.security;

import com.clone.entity.User;
import com.clone.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepo;

  public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepo) {
    this.jwtUtil = jwtUtil;
    this.userRepo = userRepo;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/api/auth/")
        || path.startsWith("/api/public/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      chain.doFilter(req, res);
      return;
    }

    String auth = req.getHeader("Authorization");

    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7).trim();

      try {
        Claims claims = jwtUtil.parse(token).getBody();

        String email = claims.getSubject();
        String tokenRole = claims.get("role", String.class);

        System.out.println("JWT SUBJECT(email) = " + email);
        System.out.println("JWT ROLE(claim) = " + tokenRole);

        if (email != null) email = email.trim().toLowerCase();

        if (email != null && !email.isBlank()) {

          User user = userRepo.findByEmail(email).orElse(null);

          if (user != null) {
        	  String dbRole = user.getRole().name();

            if (dbRole != null) dbRole = dbRole.trim().toUpperCase();

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + dbRole));
            var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            SecurityContextHolder.getContext().setAuthentication(authToken);
          } else {
            System.out.println("JWT USER NOT FOUND IN DB for email = " + email);
          }
        }

      } catch (Exception e) {
        System.out.println("JWT ERROR = " + e.getClass().getSimpleName() + " : " + e.getMessage());
      }
    }

    chain.doFilter(req, res);
  }
}

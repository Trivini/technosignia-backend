package com.clone.dto;

import lombok.*;

public class AuthDtos {

  @Getter @Setter
  public static class LoginRequest {
    private String email;
    private String password;
  }

  @Getter @Setter @AllArgsConstructor
  public static class LoginResponse {
    private String token;
    private String role;
    private String name;
  }
}


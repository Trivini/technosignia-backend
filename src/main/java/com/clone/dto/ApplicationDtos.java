package com.clone.dto;

import lombok.*;

public class ApplicationDtos {

  @Getter @Setter
  public static class ApplyRequest {
    public String resumeUrl;
    public String message;
  }
}


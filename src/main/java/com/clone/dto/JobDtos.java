package com.clone.dto;


import com.clone.entity.JobVisibility;
import lombok.*;

public class JobDtos {

  @Getter @Setter
  public static class CreateJobRequest {
    public String title;
    public String company;
    public String description;
    public String location;
    public String jobType;
    public JobVisibility visibility; // PUBLIC / TECH_ONLY
    public String externalApplyLink; // required for PUBLIC
    public String applyType; // INTERNAL / EXTERNAL

  }
}


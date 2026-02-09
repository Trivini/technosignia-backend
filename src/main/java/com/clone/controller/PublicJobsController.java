package com.clone.controller;



import com.clone.entity.Job;
import com.clone.entity.JobVisibility;
import com.clone.repository.JobRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/jobs")
public class PublicJobsController {

  private final JobRepository jobRepo;

  public PublicJobsController(JobRepository jobRepo) {
    this.jobRepo = jobRepo;
  }

  @GetMapping
  public Object listPublicJobs() {
    return jobRepo.findByVisibility(JobVisibility.PUBLIC);
  }
  
  @GetMapping("/api/jobs")
  public List<Job> publicJobs() {
    return jobRepo.findByVisibility(JobVisibility.PUBLIC);
  }
  
  @GetMapping("/api/tech/jobs")
  public List<Job> techJobs() {
    // logged-in student will call this
    return jobRepo.findAll(); 
    // OR findByVisibilityIn(PUBLIC, TECH_ONLY)
  }
  
  @GetMapping("/jobs")
  public Object allJobs() {
    return jobRepo.findByVisibilityIn(java.util.List.of(JobVisibility.PUBLIC, JobVisibility.TECH_ONLY));
  }



}


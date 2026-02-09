package com.clone.controller;

import com.clone.dto.ApplicationDtos.ApplyRequest;
import com.clone.entity.Application;
import com.clone.entity.Job;
import com.clone.entity.JobVisibility;
import com.clone.entity.User;
import com.clone.repository.ApplicationRepository;
import com.clone.repository.JobRepository;
import com.clone.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentJobsController {

  private final JobRepository jobRepo;
  private final UserRepository userRepo;
  private final ApplicationRepository appRepo;

  public StudentJobsController(JobRepository jobRepo, UserRepository userRepo, ApplicationRepository appRepo) {
    this.jobRepo = jobRepo;
    this.userRepo = userRepo;
    this.appRepo = appRepo;
  }

  // ✅ Tech student sees more jobs (PUBLIC + TECH_ONLY)
  @GetMapping("/jobs")
  public List<Job> allJobsForTechStudent() {
    return jobRepo.findByVisibilityIn(List.of(JobVisibility.PUBLIC, JobVisibility.TECH_ONLY));
  }

  // ✅ Apply only for TECH_ONLY (internal apply) — optional rule but recommended
  @PostMapping("/jobs/{jobId}/apply")
  public Object apply(@PathVariable Long jobId, @RequestBody ApplyRequest req, Authentication auth) {

    // auth principal could be String email OR UserDetails depending on your JwtAuthFilter
    String email = auth.getName(); // ✅ safest
    User user = userRepo.findByEmail(email).orElseThrow();

    Job job = jobRepo.findById(jobId).orElseThrow();

    // optional restriction: only allow internal apply for TECH_ONLY
    if (job.getVisibility() == JobVisibility.PUBLIC) {
      throw new RuntimeException("Public jobs should be applied via external link.");
    }

    Application app = Application.builder()
        .job(job)
        .user(user)
        .resumeUrl(req.getResumeUrl())
        .message(req.getMessage())
        .build();

    return appRepo.save(app);
  }
}

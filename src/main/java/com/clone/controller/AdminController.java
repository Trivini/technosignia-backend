package com.clone.controller;

import com.clone.dto.JobDtos.CreateJobRequest;
import com.clone.entity.ApplyType;
import com.clone.entity.Job;
import com.clone.entity.JobVisibility;
import com.clone.repository.ApplicationRepository;
import com.clone.repository.JobRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final JobRepository jobRepo;
  private final ApplicationRepository appRepo;

  public AdminController(JobRepository jobRepo, ApplicationRepository appRepo) {
    this.jobRepo = jobRepo;
    this.appRepo = appRepo;
  }

  @GetMapping("/jobs")
  public Object allJobs() {
    return jobRepo.findAll();
  }

  @PostMapping("/jobs")
  public Object createJob(@RequestBody CreateJobRequest req) {

    // ✅ default visibility if null
    JobVisibility visibility = (req.visibility != null) ? req.visibility : JobVisibility.PUBLIC;

    // ✅ derive applyType (if req.applyType missing)
    ApplyType applyType = parseApplyTypeOrDefault(req.applyType, visibility);

    // ✅ if INTERNAL then external link must be null/empty
    String externalLink = (applyType == ApplyType.EXTERNAL) ? req.externalApplyLink : null;

    Job job = Job.builder()
        .title(req.title)
        .company(req.company)
        .description(req.description)
        .location(req.location)
        .jobType(req.jobType)
        .visibility(visibility)
        .applyType(applyType)              // ✅ ADDED
        .externalApplyLink(externalLink)   // ✅ rule applied
        .build();

    return jobRepo.save(job);
  }

  @PutMapping("/jobs/{id}")
  public Object update(@PathVariable Long id, @RequestBody CreateJobRequest req) {
    Job job = jobRepo.findById(id).orElseThrow();

    // ✅ update basic fields
    job.setTitle(req.title);
    job.setCompany(req.company);
    job.setDescription(req.description);
    job.setLocation(req.location);
    job.setJobType(req.jobType);

    // ✅ visibility update with safe fallback
    JobVisibility visibility = (req.visibility != null) ? req.visibility : job.getVisibility();
    if (visibility == null) visibility = JobVisibility.PUBLIC;
    job.setVisibility(visibility);

    // ✅ applyType update (if missing -> derive from visibility)
    ApplyType applyType = parseApplyTypeOrDefault(req.applyType, visibility);
    job.setApplyType(applyType);

    // ✅ apply rule for link
    if (applyType == ApplyType.EXTERNAL) {
      job.setExternalApplyLink(req.externalApplyLink);
    } else {
      job.setExternalApplyLink(null);
    }

    return jobRepo.save(job);
  }

  @DeleteMapping("/jobs/{id}")
  public void delete(@PathVariable Long id) {
    jobRepo.deleteById(id);
  }

  @GetMapping("/applications")
  public Object allApplications() {
    return appRepo.findAll();
  }

  @GetMapping("/jobs/{jobId}/applications")
  public Object applicationsByJob(@PathVariable Long jobId) {
    return appRepo.findByJobId(jobId);
  }

  // ----------------- helper -----------------
  private ApplyType parseApplyTypeOrDefault(String rawApplyType, JobVisibility visibility) {
    // if frontend sends applyType
    if (rawApplyType != null && !rawApplyType.isBlank()) {
      return ApplyType.valueOf(rawApplyType.trim().toUpperCase());
    }
    // otherwise derive from visibility
    return (visibility == JobVisibility.PUBLIC) ? ApplyType.EXTERNAL : ApplyType.INTERNAL;
  }
}

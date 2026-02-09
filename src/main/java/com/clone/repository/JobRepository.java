package com.clone.repository;

import com.clone.entity.Job;
import com.clone.entity.JobVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

  List<Job> findByVisibility(JobVisibility visibility);

  List<Job> findByVisibilityIn(List<JobVisibility> visibilities);
}

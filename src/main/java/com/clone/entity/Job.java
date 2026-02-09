package com.clone.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Job {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false)
  private String title;

  @Column(nullable=false)
  private String company;

  @Column(length=5000)
  private String description;

  private String location;
  private String jobType; // Full-time, Internship, etc

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private JobVisibility visibility; // PUBLIC or TECH_ONLY
  
  @Enumerated(EnumType.STRING)
  @Column(name = "apply_type", nullable = false)
  private ApplyType applyType;


  private String externalApplyLink; // for PUBLIC jobs guest apply redirect

  private Instant createdAt;

  @PrePersist
  void onCreate() {
    if (createdAt == null) createdAt = Instant.now();
    if (visibility == null) visibility = JobVisibility.PUBLIC;
  }
}

package com.clone.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false)
  @JoinColumn(name="job_id")
  private Job job;

  @ManyToOne(optional=false)
  @JoinColumn(name="user_id")
  private User user; // only TECH_STUDENT

  private String resumeUrl; // simple approach (later file upload add)
  private String message;

  private Instant appliedAt;

  @PrePersist
  void onCreate() {
    if (appliedAt == null) appliedAt = Instant.now();
  }
}

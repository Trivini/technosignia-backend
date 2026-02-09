package com.clone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "technosignia_students")
public class TechnosigniaStudent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}

package com.clone.repository;

import com.clone.entity.TechnosigniaStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnosigniaStudentRepository extends JpaRepository<TechnosigniaStudent, Long> {
  boolean existsByEmail(String email);
}

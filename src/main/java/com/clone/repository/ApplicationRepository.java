package com.clone.repository;



import com.clone.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
  List<Application> findByJobId(Long jobId);
}



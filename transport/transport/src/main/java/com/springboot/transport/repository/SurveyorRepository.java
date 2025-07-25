
package com.springboot.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.transport.entity.Surveyor;

@Repository
public interface SurveyorRepository extends JpaRepository<Surveyor, Integer> {
@Query("SELECT s FROM Surveyor s WHERE s.status = 'active'")
List<Surveyor> findAllActiveSurveyors();

}
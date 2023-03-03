package org.example.repository;

import org.example.model.entity.Exam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExamRepository extends CrudRepository<Exam, String> {
    @Query(value = "select id from exam order by created_date asc", nativeQuery = true)
    List<String> findAllExamId();
}

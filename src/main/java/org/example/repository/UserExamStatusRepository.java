package org.example.repository;

import org.example.model.entity.UserExamStatus;
import org.springframework.data.repository.CrudRepository;

public interface UserExamStatusRepository extends CrudRepository<UserExamStatus, String> {
}

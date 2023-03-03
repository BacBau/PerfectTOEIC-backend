package org.example.repository;

import org.example.model.entity.MiniTest;
import org.springframework.data.repository.CrudRepository;

public interface MiniTestRepository extends CrudRepository<MiniTest, String> {
}

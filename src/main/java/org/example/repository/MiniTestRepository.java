package org.example.repository;

import org.example.model.entity.MiniTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MiniTestRepository extends CrudRepository<MiniTest, String> {
    @Query("SELECT id FROM MiniTest ORDER BY createdDate DESC")
    List<String> getAllPublicId();
}

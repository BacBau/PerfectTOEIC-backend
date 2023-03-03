package org.example.repository;

import org.example.model.entity.TestPart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PartRepository extends CrudRepository<TestPart, String> {
    @Query(value = "select id from test_part where part = ? order by created_date asc", nativeQuery = true)
    List<String> findAllPartIdByPartNumber(int partNumber);
}

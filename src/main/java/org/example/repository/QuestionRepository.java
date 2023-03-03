package org.example.repository;

import org.example.model.entity.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, String> {

    //@Query(value = "select * from question where exam_id = ? and parent_id == ?", nativeQuery = true)
    List<Question> findAllByExamId(String examId);

    List<Question> findAllByParentIdOrderByQuestionNumber(String parentId);
    @Query(value = "SELECT count(*) FROM question", nativeQuery = true)
    Long countAll();
}

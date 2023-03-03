package org.example.service.mapper;

import org.example.model.entity.Question;
import org.example.model.response.QuestionResponse;
import org.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionMapper {

    @Autowired
    QuestionRepository questionRepository;

    public QuestionResponse questionToQuestionResponse(Question question) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setPublicId(question.getPublicId());
        questionResponse.setText(question.getText());
        questionResponse.setImage(question.getImage());
        questionResponse.setSound(question.getSound());
        questionResponse.setHint(question.getHint());
        questionResponse.setAnswer(question.getAnswer());
        questionResponse.setExamId(question.getExamId());
        questionResponse.setParentId(question.getParentId());
        questionResponse.setHasChild(question.getHasChild());
        questionResponse.setQuestionNumber(question.getQuestionNumber());
        if (question.getHasChild() == 1) {
            questionResponse.setChildQuestion(questionRepository.findAllByParentIdOrderByQuestionNumber(question.getPublicId()));
        } else {
            questionResponse.setChildQuestion(new ArrayList<>());
        }
        return questionResponse;
    }
}

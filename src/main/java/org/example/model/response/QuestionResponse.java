package org.example.model.response;

import lombok.Data;
import org.example.model.entity.Question;

import java.util.List;

@Data
public class QuestionResponse {
    List<Question> childQuestion;
    private String publicId;
    private String text;
    private String image;
    private int questionNumber;
    private String sound;
    private String hint;
    private String answer;
    private int correctAnswer;
    private int part;
    private String examId;
    private String parentId;
    private int hasChild;
}

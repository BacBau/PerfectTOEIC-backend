package org.example.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportQuestionRequest {
    private String text;
    private String image;
    private String sound;
    private String hint;
    private int part;
    private List<String> answer;
    private int correctAnswer;
}

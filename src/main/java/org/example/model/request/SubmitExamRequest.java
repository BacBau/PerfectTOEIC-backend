package org.example.model.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitExamRequest {
    private String examId;
    private List<String> answer;
}

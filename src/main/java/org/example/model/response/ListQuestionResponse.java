package org.example.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListQuestionResponse {
    private List<QuestionResponse> questionList;
}

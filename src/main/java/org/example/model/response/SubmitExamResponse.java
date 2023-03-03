package org.example.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitExamResponse {
    private int scoreListening;
    private int scoreReading;
}

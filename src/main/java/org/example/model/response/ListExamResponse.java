package org.example.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ListExamResponse {
    List<String> id;
}

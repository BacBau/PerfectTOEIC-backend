package org.example.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ListPostRequest {
    private List<PostRequest> posts;
}

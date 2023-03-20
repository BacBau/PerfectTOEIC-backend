package org.example.model.request;

import lombok.Data;
import org.example.model.entity.Post;
import org.example.utils.Utils;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdatePostRequest extends PostRequest {
    @NotNull(message = "id is required!")
    private String id;

    public void updatePost(Post post) {
        post.setTitle(this.getTitle());
        post.setHtml(this.getHtml());
        post.setType(this.getType());
        post.setDescription(this.getDescription());
        post.setPreviewImageUrl(this.getPreviewImageUrl());
    }
}

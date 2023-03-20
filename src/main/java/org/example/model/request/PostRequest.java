package org.example.model.request;

import lombok.Data;
import org.example.model.entity.Post;
import org.example.utils.Utils;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PostRequest {
    @NotNull(message = "type is required!")
    private Post.Type type;
    @NotNull(message = "title is required!")
    private String title;
    @NotNull(message = "html is required")
    private String html;
    @NotNull(message = "previewImageUrl")
    private String previewImageUrl;

    @NotNull(message = "description is required!!")
    private String description;


    public Post toPost() {
        Post post = new Post();
        post.setHtml(this.html);
        post.setTitle(this.title);
        post.setType(this.type);
        post.setPreviewImageUrl(this.previewImageUrl);
        post.setDescription(this.description);
        return post;
    }

}

package org.example.solr;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.model.entity.Post;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.Date;


@Data
public class PostIndex {
    private String id;
    private String html;
    private String content;
    private Post.Type type;
    private String title;
    private String previewImageUrl;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "UTC")
    private Date createdDate;
    private String description;

    public static PostIndex from(Post post) {
        PostIndex postIndex = new PostIndex();
        BeanUtils.copyProperties(post, postIndex);
        postIndex.setCreatedDate(Date.from(post.getCreatedDate()));
        return postIndex;
    }
}

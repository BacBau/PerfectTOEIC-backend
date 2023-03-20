package org.example.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Post extends AbstractEntity {
    @Lob
    private String html;
    private String content;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String title;
    @Lob
    private String previewImageUrl;
    @Lob
    private String description;
    public enum Type {
        BLOG,
        LISTENING_TIP,
        READING_TIP,
        INTRODUCTION
    }
}

package org.example.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Question extends AbstractEntity {
    private String publicId;
    @Lob
    private String text;
    private String image;
    private String sound;
    @Column(length = 2000)
    private String hint;
    @Column(length = 2000)
    private String answer;
    private int correctAnswer;
    private int part;
    private String examId;
    private String parentId;

    private int hasChild;

    private int questionNumber;
}

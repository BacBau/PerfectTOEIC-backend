package org.example.model.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class UserQuestionStatus extends AbstractEntity {
    private String userId;
    private String questionId;
    private boolean isCorrected;
}

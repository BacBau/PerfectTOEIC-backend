package org.example.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class UserExamStatus extends AbstractEntity {
    private String userId;
    private String examId;
    private int numberCorrect;
    private int numberNotFinish;
    private int scoreListening;
    private int scoreReading;
    private int time;
}

package org.example.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class TestPart extends AbstractEntity {
    private int part;
    private String publicId;
}

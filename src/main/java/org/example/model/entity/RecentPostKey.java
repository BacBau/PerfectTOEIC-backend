package org.example.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
public class RecentPostKey implements Serializable {
    @Column(name = "username")
    private String username;
    @Column(name = "post_id")
    private String postId;

    public RecentPostKey(String postId, String username) {
        this.postId = postId;
        this.username = username;
    }

    public RecentPostKey() {

    }
}

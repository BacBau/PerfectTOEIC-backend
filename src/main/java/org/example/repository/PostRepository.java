package org.example.repository;

import org.example.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, String> {

    List<Post> findAllByType(Post.Type type, Pageable pageable);
}

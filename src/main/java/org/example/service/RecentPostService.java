package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.model.entity.Post;
import org.example.model.entity.RecentPost;
import org.example.model.entity.RecentPostKey;
import org.example.repository.PostRepository;
import org.example.repository.RecentPostRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class RecentPostService {

    private final RecentPostRepository repository;
    private final PostRepository postRepository;

    public RecentPostService(RecentPostRepository repository,
                             PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Async
    public void addToRecentPost(String currentNick, String postIdToAdd) {
        if (currentNick == null) return;
        List<RecentPost> recentPosts = repository.getByUsername(currentNick);
        if (recentPosts.size() == 6)
            repository.delete(recentPosts.get(5));

        RecentPost recentPost = new RecentPost();
        recentPost.setCreatedDate(new Date().toInstant());
        RecentPostKey recentPostKey = new RecentPostKey();
        recentPostKey.setPostId(postIdToAdd);
        recentPostKey.setUsername(currentNick);
        recentPost.setRecentPostKey(recentPostKey);
        repository.save(recentPost);
        log.info("save recent Post done!!!");
    }

    public List<Post> getRecentPost(String currentNick) {
        List<Post> result = new ArrayList<>();
        repository.getByUsername(currentNick).forEach(recentPost ->
                postRepository.findById(recentPost.getRecentPostKey().getPostId()).ifPresent(post -> result.add(post)));
        return result;
    }
}

package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.common.solr.exception.RepositoryException;
import org.example.config.Constants;
import org.example.exception.EnglishExamException;
import org.example.model.entity.Post;
import org.example.model.entity.RecentPost;
import org.example.model.error.ErrorCode;
import org.example.model.request.PostRequest;
import org.example.model.request.UpdatePostRequest;
import org.example.repository.PostRepository;
import org.example.service.PostService;
import org.example.service.RecentPostService;
import org.example.solr.PostIndex;
import org.example.solr.PostSolrRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostSolrRepository postSolrRepository;
    private final RecentPostService recentPostService;

    public PostServiceImpl(PostRepository postRepository,
                           PostSolrRepository postSolrRepository,
                           RecentPostService recentPostService) {
        this.postRepository = postRepository;
        this.postSolrRepository = postSolrRepository;
        this.recentPostService = recentPostService;
    }

    public Post createPost(PostRequest postRequest, boolean isJob) {
        Post post = postRequest.toPost();
        if (isJob)
            post.setCreatedBy("ADMIN");
        else
            post.setCreatedBy(Constants.getCurrentUser());
        return trySavePostToSolr(postRepository.save(post));
    }

    public void deleteAllDataInSolr() {
        try {
            postSolrRepository.deleteByQuery("*:*");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Post trySavePostToSolr(Post post) {
        try {
            if (post.getType().equals(Post.Type.INTRODUCTION))
                return post;
            postSolrRepository.save(PostIndex.from(post));
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
        return post;
    }

    private void tryDeletePostInSolr(String id) {
        try {
            postSolrRepository.deleteById(id);
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteById(String id) {
        postRepository.deleteById(id);
        tryDeletePostInSolr(id);
    }

    public Post update(UpdatePostRequest updatePostRequest) {
        Post post = postRepository.findById(updatePostRequest.getId()).orElseThrow(() ->
                new EnglishExamException(ErrorCode.POST_NOT_FOUND));
        updatePostRequest.updatePost(post);
        return trySavePostToSolr(postRepository.save(post));
    }

    public List<Post> getByType(Post.Type type, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        return postRepository.findAllByType(type, pageable);
    }

    public Post getDetail(String id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new EnglishExamException(ErrorCode.POST_NOT_FOUND));
        String currentNick = Constants.getCurrentUser();
        recentPostService.addToRecentPost(currentNick, id);
        return post;
    }

    public Post getIntroduction() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Post> posts = postRepository.findAllByType(Post.Type.INTRODUCTION, pageable);
        if (posts.isEmpty()) return null;
        return posts.get(0);
    }

    public List<PostIndex> search(String keyword) {
        return postSolrRepository.search(keyword, 10000);
    }

    public boolean needLoadPosts() {
        return !postRepository.findAll().iterator().hasNext();
    }

}

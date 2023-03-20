package org.example.controller;

import org.example.auth.AuthoritiesConstants;
import org.example.exception.EnglishExamException;
import org.example.model.entity.Post;
import org.example.model.error.ErrorCode;
import org.example.model.request.PostRequest;
import org.example.model.request.UpdatePostRequest;
import org.example.service.PostService;
import org.example.solr.PostIndex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@RequestBody @Valid PostRequest postRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(postRequest, false));
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable String postId) {
        postService.deleteById(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Secured(AuthoritiesConstants.ROLE_ADMIN)
    @PutMapping("/post")
    public ResponseEntity<Post> updatePost(@RequestBody @Valid UpdatePostRequest updatePostRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.update(updatePostRequest));
    }



    @GetMapping("/posts/type/{type}")
    public ResponseEntity<List<Post>> getByTypeAndPaging(@RequestParam(name = "page_index", defaultValue = "1", required = false) int pageIndex,
                                                         @RequestParam(name = "page_size", defaultValue = "10", required = false) int pageSize,
                                                         @PathVariable String type) {

        Post.Type postType = null;
        try {
            postType = Post.Type.valueOf(type);
        } catch (Exception e) {
            throw new EnglishExamException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(postService.getByType(postType, pageIndex, pageSize));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> getDetail(@PathVariable String postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getDetail(postId));
    }

    @GetMapping("/posts/search")
    public ResponseEntity<List<PostIndex>> search(@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.search(keyword));
    }
}

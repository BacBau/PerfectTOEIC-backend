package org.example.controller;

import org.example.model.entity.Post;
import org.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class HomeController {
    @Autowired
    private PostService postService;

    @GetMapping("/home/introduction")
    public ResponseEntity<Post> getIntroduction() {
        return ResponseEntity.ok(postService.getIntroduction());
    }
}

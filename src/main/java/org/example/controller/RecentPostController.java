package org.example.controller;

import org.example.config.Constants;
import org.example.model.entity.Post;
import org.example.service.RecentPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
public class RecentPostController {
    private final RecentPostService recentPostService;

    public RecentPostController(RecentPostService recentPostService) {
        this.recentPostService = recentPostService;
    }

    @GetMapping("/recent-post")
    public ResponseEntity<List<Post>> getRecentPost() {
        String currentNick = Constants.getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(recentPostService.getRecentPost(currentNick));
    }
}

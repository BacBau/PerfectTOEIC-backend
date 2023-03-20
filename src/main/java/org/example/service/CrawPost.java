package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.example.model.request.ListPostRequest;
import org.example.model.request.PostRequest;
import org.example.utils.Utils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CrawPost {

    private final PostService postService;

    public CrawPost(PostService postService) {
        this.postService = postService;
    }

    @PostConstruct
    public void initPosts() {
        if (!postService.needLoadPosts()) return;
        postService.deleteAllDataInSolr();
        List<PostRequest> postRequests = getPostRequestsFromFile();
        postRequests.forEach(postRequest ->
                postService.createPost(postRequest, true));
        log.info("Create post done!!");
    }

    private List<PostRequest> getPostRequestsFromFile() {
        try(InputStream in = CrawPost.class.getResourceAsStream("/templates/posts.json")){
            ObjectMapper mapper = new ObjectMapper();
            ListPostRequest jsonNode = mapper.readValue(in, ListPostRequest.class);
            List<PostRequest> postRequests = jsonNode.getPosts();
            List<PostRequest> toSave = new ArrayList<>();
            postRequests.forEach(postRequest -> toSave.add(html(postRequest)));
            return postRequests;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private PostRequest html(PostRequest postRequest) {
        String html = postRequest.getHtml().replace("\n", "").replace("\"", "'");
        postRequest.setHtml(html);
        return postRequest;
    }
}

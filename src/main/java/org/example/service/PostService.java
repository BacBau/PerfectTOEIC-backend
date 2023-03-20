package org.example.service;

import org.example.model.entity.Post;
import org.example.model.request.PostRequest;
import org.example.model.request.UpdatePostRequest;
import org.example.solr.PostIndex;

import java.util.List;

public interface PostService {
    Post createPost(PostRequest postRequest, boolean isJob);
    void deleteAllDataInSolr();
    void deleteById(String id);
    Post update(UpdatePostRequest updatePostRequest);
    List<Post> getByType(Post.Type type, int pageIndex, int pageSize);
    Post getDetail(String id);
    Post getIntroduction();
    List<PostIndex> search(String keyword);
    boolean needLoadPosts();
}

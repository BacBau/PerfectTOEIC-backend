package org.example.repository;

import org.example.model.entity.RecentPost;
import org.example.model.entity.RecentPostKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentPostRepository extends CrudRepository<RecentPost, RecentPostKey> {

    @Query("SELECT rp FROM RecentPost rp WHERE rp.recentPostKey.username = :username ORDER BY rp.createdDate DESC")
    List<RecentPost> getByUsername(@Param("username") String username);
}

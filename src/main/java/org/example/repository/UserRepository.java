package org.example.repository;

import org.example.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndProvider(String username, User.Provider provider);

    Page<User> findAll(Pageable pageable);

    @Modifying
    void deleteById(String id);

    @Query("select c.id from User c where c.username = :username")
    String getIdByUsername(String username);

    @Query("select c.username from User c where c.id = :userId")
    String getUsernameById(String userId);

    List<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String username, String fullName);

    List<User> findAllByUsernameIn(List<String> usernames);
}

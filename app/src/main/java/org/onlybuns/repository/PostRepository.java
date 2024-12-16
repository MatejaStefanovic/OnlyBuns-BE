package org.onlybuns.repository;

import org.onlybuns.enums.UserRole;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
}


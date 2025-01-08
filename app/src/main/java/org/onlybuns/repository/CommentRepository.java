package org.onlybuns.repository;

import org.onlybuns.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository  extends JpaRepository<Comment, Long> {

    @Query("SELECT COUNT(DISTINCT c.user) FROM Comment c WHERE c.user.numberOfPosts = 0")
    long countUsersWithOnlyComments();
}

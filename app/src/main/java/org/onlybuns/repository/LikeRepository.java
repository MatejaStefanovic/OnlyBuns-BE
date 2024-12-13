package org.onlybuns.repository;

import org.onlybuns.model.Like;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository  extends JpaRepository<Like, Long> {
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    void deleteByUserAndPost(@Param("userId") long userId, @Param("postId") long postId);

}

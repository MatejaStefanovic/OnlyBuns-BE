package org.onlybuns.repository;

import org.onlybuns.model.Post;
import org.onlybuns.model.PostLikeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeUserRepository extends JpaRepository<PostLikeUser, Long> {
    void deleteByPostIdAndUsername(Long postId, String username);

    List<PostLikeUser> findByPostId(Long postId);
    @Query("SELECT COUNT(p) FROM PostLikeUser p WHERE p.post.id = :postId")
    long countLikesByPostId(@Param("postId") Long postId);
}

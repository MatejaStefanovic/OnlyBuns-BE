package org.onlybuns.repository;

import jakarta.transaction.Transactional;
import org.onlybuns.model.Like;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface LikeRepository  extends JpaRepository<Like, Long> {
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    void deleteByUserAndPost(@Param("userId") long userId, @Param("postId") long postId);


    @Query("SELECT l.user as user, COUNT(l) as likeCount " +
       "FROM Like l " +
       "WHERE l.creationDateTime >= :sevenDaysAgo " +
       "GROUP BY l.user " +
       "ORDER BY COUNT(l) DESC")
    List<Object[]> findUsersWithMostLikesLastWeek(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

}

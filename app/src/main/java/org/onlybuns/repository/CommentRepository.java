package org.onlybuns.repository;

import org.onlybuns.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long> {

    @Query("SELECT COUNT(DISTINCT c.user) FROM Comment c WHERE c.user.numberOfPosts = 0")
    long countUsersWithOnlyComments();

    @Query("SELECT DATE(p.creationDateTime), COUNT(*) " +
            "FROM Comment p " +
            "WHERE DATE(p.creationDateTime) BETWEEN :startDate AND :endDate "+
            "GROUP BY DATE(p.creationDateTime) " +
            "ORDER BY DATE(p.creationDateTime)")
    List<Object[]> getDailyCommentCounts(LocalDateTime startDate, LocalDateTime endDate);

}

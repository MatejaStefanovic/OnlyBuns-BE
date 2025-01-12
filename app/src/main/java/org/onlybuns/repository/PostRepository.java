package org.onlybuns.repository;

import org.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.creationDateTime >= :sevenDaysAgo ORDER BY p.likes DESC")
    List<Post> findTopFivePostsLastWeek(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.likes DESC")
    List<Post> findTopTenPostsAllTime(Pageable pageable);
}


package org.onlybuns.repository;

import jakarta.persistence.LockModeType;
import org.onlybuns.enums.UserRole;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DATE(p.creationDateTime), COUNT(*) AS postCount " +
            "FROM Post p " +
            "WHERE DATE(p.creationDateTime) BETWEEN :startDate AND :endDate "+
            "GROUP BY DATE(p.creationDateTime) " +
            "ORDER BY DATE(p.creationDateTime)")
    List<Object[]> getDailyPostCounts(LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT p FROM Post p WHERE p.creationDateTime >= :sevenDaysAgo ORDER BY p.likes DESC")
    List<Post> findTopFivePostsLastWeek(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.likes DESC")
    List<Post> findTopTenPostsAllTime(Pageable pageable);
    List<Post> findAllByUser(User user);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdForUpdate(@Param("id") Long id);

}


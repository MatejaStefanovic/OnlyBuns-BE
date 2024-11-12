package org.onlybuns.repository;

import org.onlybuns.model.Like;
import org.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository  extends JpaRepository<Like, Long> {
}

package org.onlybuns.repository;

import org.onlybuns.model.Image;
import org.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}

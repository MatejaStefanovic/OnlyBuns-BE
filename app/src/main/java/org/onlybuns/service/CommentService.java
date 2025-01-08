package org.onlybuns.service;

import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.LikeRepository;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public long countUsersWithOnlyComments() {
        return commentRepository.countUsersWithOnlyComments();
    }
}

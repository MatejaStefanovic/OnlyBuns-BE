package org.onlybuns.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.hibernate.annotations.Parameter;
import org.onlybuns.DTOs.LoginRequestDTO;
import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.exceptions.UserRegistration.UnauthorizedUserException;
import org.onlybuns.model.Location;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.onlybuns.service.CommentService;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/weekly")
    public ResponseEntity<Integer> getWeeklyStatistic() throws IOException {

        int stat = commentService.getCommentStatisticsWeekly();
        return ResponseEntity.ok(stat);
    }

    @GetMapping("/monthly")
    public ResponseEntity<Integer> getMonthlyStatistic() throws IOException {

        int stat = commentService.getCommentStatisticsMonthly();
        return ResponseEntity.ok(stat);
    }

    @GetMapping("/yearly")
    public ResponseEntity<Integer> getYearlyStatistic() throws IOException {

        int stat = commentService.getCommentStatisticsYearly();
        return ResponseEntity.ok(stat);
    }

}

package org.onlybuns.service;

import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.LikeRepository;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

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

    public int getCommentStatisticsYearly() {
        List<Object[]> dailyCommentCounts =    commentRepository.getDailyCommentCounts(LocalDateTime.now().minusYears(1), LocalDateTime.now());
        Map<String, Long> yearlyCounts = new HashMap<>();

        for (Object[] row : dailyCommentCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
            long commentCount = (long) row[1];
            String year = String.valueOf(date.getYear());

            yearlyCounts.put(year, yearlyCounts.getOrDefault(year, 0L) + commentCount);
        }
        System.out.println("Y: " + yearlyCounts);
        long sum = 0;
        for (long count : yearlyCounts.values()) {
            sum += count;
        }
        int totalMonths = yearlyCounts.size();
        return (int) Math.round((double) sum / totalMonths);
    }



    public int getCommentStatisticsMonthly() {
        List<Object[]> dailyCommentCounts =    commentRepository.getDailyCommentCounts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        Map<String, Long> monthlyCounts = new HashMap<>();

        for (Object[] row : dailyCommentCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
            long commentCount = (long) row[1];
            int year = date.getYear();
            int month = date.getMonthValue();
            String yearMonth = year + "-" + String.format("%02d", month); //"GODINA-MJESEC"
            monthlyCounts.put(yearMonth, monthlyCounts.getOrDefault(yearMonth, 0L) + commentCount);
        }

        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int year = current.getYear();
            int month = current.getMonthValue();
            String yearMonth = year + "-" + String.format("%02d", month);
            monthlyCounts.putIfAbsent(yearMonth, 0L);
            current = current.plusMonths(1);
        }
        System.out.println("M: " + monthlyCounts);
        long sum = 0;
        for (long count : monthlyCounts.values()) {
            sum += count;
        }
        int totalMonths = monthlyCounts.size();
        return (int) Math.round((double) sum / totalMonths);
    }

    public int getCommentStatisticsWeekly() {
        List<Object[]> dailyCommentCounts =    commentRepository.getDailyCommentCounts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        Map<String, Long> weeklyCounts = new HashMap<>();

        for (Object[] row : dailyCommentCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
            long commentCount = (long) row[1];
            int year = date.getYear();
            int week = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String yearWeek = year + "-" + String.format("%02d", week); //"GODINA-SEDMICA"
            weeklyCounts.put(yearWeek, weeklyCounts.getOrDefault(yearWeek, 0L) + commentCount);
        }
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int year = current.getYear();
            int week = current.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String yearWeek = year + "-" + String.format("%02d", week);
            weeklyCounts.putIfAbsent(yearWeek, 0L);
            current = current.plusWeeks(1);
        }
        System.out.println("W: " + weeklyCounts);
        long sum = 0;
        for (long count : weeklyCounts.values()) {
            sum += count;
        }
        int totalWeeks = weeklyCounts.size();
        return (int) Math.round((double) sum / totalWeeks);
    }

}

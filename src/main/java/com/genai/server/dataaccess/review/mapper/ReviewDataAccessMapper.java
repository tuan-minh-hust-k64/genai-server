package com.genai.server.dataaccess.review.mapper;

import com.genai.server.dataaccess.review.entity.ReviewEntity;
import com.genai.server.models.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class ReviewDataAccessMapper {
    public Review reviewEntityToReview(ReviewEntity reviewEntity) {
        return Review.builder()
                .reviewId(reviewEntity.getReviewId())
                .id(reviewEntity.getId())
                .content(reviewEntity.getContent())
                .createdAt(reviewEntity.getCreatedAt())
                .label(reviewEntity.getLabel())
                .projectId(reviewEntity.getProjectId())
                .rate(reviewEntity.getRate())
                .status(reviewEntity.getStatus())
                .build();
    }
    public ReviewEntity reviewToReviewEntity(Review review) {
        return ReviewEntity.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .id(review.getId())
                .label(review.getLabel())
                .projectId(review.getProjectId())
                .rate(review.getRate())
                .status(review.getStatus())
                .build();
    }

}

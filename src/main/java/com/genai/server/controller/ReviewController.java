package com.genai.server.controller;

import com.genai.server.common.valueobject.ReviewStatus;
import com.genai.server.dataaccess.review.adapter.ReviewRepositoryImpl;
import com.genai.server.dataaccess.review.dto.CreateReviewsCommand;
import com.genai.server.dataaccess.review.dto.ReviewsNotLabelResponse;
import com.genai.server.gcp.BigqueryHelper;
import com.genai.server.models.Review;
import com.genai.server.services.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping(value = "/reviews")
public class ReviewController {
    private final ReviewRepositoryImpl reviewRepository;
    private final ReviewService reviewService;
    private final BigqueryHelper bigqueryHelper;
    public ReviewController(ReviewRepositoryImpl reviewRepository, ReviewService reviewService, BigqueryHelper bigqueryHelper) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.bigqueryHelper = bigqueryHelper;
    }
    @GetMapping(value = "/test")
    public ResponseEntity<String> testApi() throws Exception {
        reviewService.classificationReviews();
        return ResponseEntity.ok("qư");
    }
    @GetMapping(value = "/test1")
    public ResponseEntity<String> testApi1() throws Exception {
        bigqueryHelper.runCreateTable("AllProject_OverviewMetric", "GOOGLE_PLAYSTORE_play_store_labeled_review");
//        bigqueryHelper.deleteTable("AllProject_OverviewMetric", "GOOGLE_PLAYSTORE_play_store_labeled_review");
        return ResponseEntity.ok("llll");
    }
    @GetMapping(value = "/test2")
    public ResponseEntity<String> testApi2() throws Exception {
//        bigqueryHelper.runCreateTable("AllProject_OverviewMetric", "GOOGLE_PLAYSTORE_play_store_labeled_review");
        bigqueryHelper.deleteTable("AllProject_OverviewMetric", "GOOGLE_PLAYSTORE_play_store_labeled_review");
        return ResponseEntity.ok("llll");
    }
    @GetMapping
    public ResponseEntity<ReviewsNotLabelResponse> getReviewsNotLabel() {
        Optional<List<Review>> reviews = reviewRepository.findByStatus();
        return reviews.map(reviewList -> ResponseEntity.ok(
                ReviewsNotLabelResponse.builder()
                        .reviews(reviewList)
                        .build()
        )).orElseGet(() -> ResponseEntity.ok(ReviewsNotLabelResponse.builder().reviews(List.of()).build()));
    }
    @PostMapping
    public ResponseEntity<CreateReviewsCommand> createReviews(@RequestBody CreateReviewsCommand createReviewsCommand) {
        List<Review> reviews = reviewRepository.save(createReviewsCommand.getReviews().stream()
                        .peek(review -> {
                            review.setId(UUID.randomUUID());
                            if (review.getLabel().trim().isEmpty()) review.setStatus(ReviewStatus.NOT_LABELED);
                            else review.setStatus(ReviewStatus.LABELED);
                        })
                .toList());
        return ResponseEntity.ok(CreateReviewsCommand.builder()
                        .reviews(reviews)
                .build());
    }
}

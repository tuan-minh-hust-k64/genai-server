package com.genai.server.dataaccess.review.adapter;


import com.genai.server.common.valueobject.ReviewStatus;
import com.genai.server.dataaccess.review.entity.ReviewEntity;
import com.genai.server.dataaccess.review.mapper.ReviewDataAccessMapper;
import com.genai.server.dataaccess.review.repository.ReviewJpaRepository;
import com.genai.server.models.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
@Slf4j
public class ReviewRepositoryImpl implements ReviewRepository {
    private final ReviewJpaRepository reviewJpaRepository;
    private final ReviewDataAccessMapper reviewDataAccessMapper;

    public ReviewRepositoryImpl(ReviewJpaRepository reviewJpaRepository,
                                ReviewDataAccessMapper reviewDataAccessMapper) {
        this.reviewJpaRepository = reviewJpaRepository;
        this.reviewDataAccessMapper = reviewDataAccessMapper;
    }

    @Override
    @Transactional
    public List<Review> save(List<Review> reviews) {
        List<ReviewEntity> reviewEntities = reviews.stream().map(reviewDataAccessMapper::reviewToReviewEntity).toList();

        return reviewJpaRepository.saveAll(reviewEntities).stream()
                .map(reviewDataAccessMapper::reviewEntityToReview)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Review>> findByStatus() {
        return Optional.of(reviewJpaRepository.findByStatus(ReviewStatus.NOT_LABELED)
                .orElseThrow(() -> new RuntimeException("Reviews not label empty!")).stream()
                .map(reviewDataAccessMapper::reviewEntityToReview)
                .collect(Collectors.toList()));
    }
}

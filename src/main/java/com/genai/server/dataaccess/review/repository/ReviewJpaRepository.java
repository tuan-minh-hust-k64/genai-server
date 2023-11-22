package com.genai.server.dataaccess.review.repository;

import com.genai.server.common.valueobject.ReviewStatus;
import com.genai.server.dataaccess.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {
    Optional<List<ReviewEntity>> findByStatus(ReviewStatus reviewStatus);
}

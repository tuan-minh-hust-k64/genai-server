package com.genai.server.dataaccess.review.adapter;

import com.genai.server.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    List<Review> save(List<Review> reviews);
    Optional<List<Review>> findByStatus();
}

package com.genai.server.dataaccess.review.dto;

import com.genai.server.models.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewsNotLabelResponse {
    private final List<Review> reviews;
}

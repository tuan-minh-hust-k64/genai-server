package com.genai.server.dataaccess.review.dto;

import com.genai.server.models.Review;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class CreateReviewsCommand {
    @NotNull
    private final List<Review> reviews;
}

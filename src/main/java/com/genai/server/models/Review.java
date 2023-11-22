package com.genai.server.models;

import com.genai.server.common.valueobject.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Review {
    private UUID id;
    private final String reviewId;
    private final String content;
    private final int rate;
    private final String label;
    private final ZonedDateTime createdAt;
    private ReviewStatus status;
    private String projectId;

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", reviewId='" + reviewId + '\'' +
                ", content='" + content + '\'' +
                ", rate=" + rate +
                ", labels=" + label +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}

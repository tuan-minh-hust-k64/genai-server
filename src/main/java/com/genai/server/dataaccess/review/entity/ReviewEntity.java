package com.genai.server.dataaccess.review.entity;

import com.genai.server.common.valueobject.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "review")
public class ReviewEntity {
    @Id
    private UUID id;
    private String reviewId;
    private String content;
    private int rate;
    private String label;
    private String projectId;
    private ZonedDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity that = (ReviewEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(reviewId, that.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reviewId);
    }

    @Override
    public String toString() {
        return "ReviewEntity{" +
                "id=" + id +
                ", reviewId='" + reviewId + '\'' +
                ", content='" + content + '\'' +
                ", rate=" + rate +
                ", labels=" + label +
                ", projectId='" + projectId + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}

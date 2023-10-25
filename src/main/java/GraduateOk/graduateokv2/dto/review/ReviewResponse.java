package GraduateOk.graduateokv2.dto.review;

import GraduateOk.graduateokv2.domain.Review;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReviewResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Register {

        Long id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Detail {

        Long id;

        String author;

        String subject;

        String title;

        String content;

        Float starScore;

        public static ReviewResponse.Detail of(Review review) {
            return Detail.builder()
                    .id(review.getId())
                    .author(review.getUser().getNickname())
                    .subject(review.getSubject().getName())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .starScore(review.getStarScore())
                    .build();
        }
    }
}

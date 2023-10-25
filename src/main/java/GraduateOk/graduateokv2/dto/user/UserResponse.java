package GraduateOk.graduateokv2.dto.user;

import GraduateOk.graduateokv2.domain.Review;
import GraduateOk.graduateokv2.domain.User;
import GraduateOk.graduateokv2.dto.review.ReviewResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Token {

        String tokenType; // Bearer

        String accessToken;

        String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Join {

        Long id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {

        Long id;

        String email;

        String nickname;

        UserResponse.Token tokenInfo;
    }

    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UpdateInfo {

        Long id;

        String email;

        String nickname;

        public static UserResponse.UpdateInfo of(User user) {
            return UpdateInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .build();
        }
    }

    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Info extends UpdateInfo {

        List<ReviewResponse.Detail> reviewList;

        public static UserResponse.Info of(User user, List<Review> reviewList) {
            return Info.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .reviewList(reviewList.stream().map(ReviewResponse.Detail::of).collect(Collectors.toList()))
                    .build();
        }
    }
}

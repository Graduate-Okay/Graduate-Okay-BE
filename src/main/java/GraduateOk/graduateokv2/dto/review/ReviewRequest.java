package GraduateOk.graduateokv2.dto.review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;

@UtilityClass
public class ReviewRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Register {

        @NotNull
        Long subjectId;

        @NotNull
        String title;

        @NotNull
        String content;

        @NotNull
        Float starScore;
    }
}

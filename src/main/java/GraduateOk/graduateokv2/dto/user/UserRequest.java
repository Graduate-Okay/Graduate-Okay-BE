package GraduateOk.graduateokv2.dto.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;

@UtilityClass
public class UserRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Join {

        @javax.validation.constraints.Email
        @NotNull
        String email;

        @NotNull
        String password;

        @NotNull
        String nickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Email {

        @javax.validation.constraints.Email
        @NotNull
        String email;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {

        @javax.validation.constraints.Email
        @NotNull
        String email;

        @NotNull
        String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Update {

        String password;

        String nickname;
    }
}

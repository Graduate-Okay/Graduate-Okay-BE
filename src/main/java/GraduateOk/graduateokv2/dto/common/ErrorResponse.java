package GraduateOk.graduateokv2.dto.common;

import GraduateOk.graduateokv2.exception.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {

    HttpStatus status;

    String code;

    String message;

    public ErrorResponse(Error error) {
        this.status = error.getStatus();
        this.code = error.getCode();
        this.message = error.getMessage();
    }
}

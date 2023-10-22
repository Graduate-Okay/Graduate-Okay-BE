package GraduateOk.graduateokv2.exception;

import GraduateOk.graduateokv2.dto.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        Error error = e.getError();
        ErrorResponse errorResponse = new ErrorResponse(error.getStatus(), error.getCode(), error.getMessage());
        return new ResponseEntity<>(errorResponse, e.getError().getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> ServerExceptionHandler(Exception e) {
        log.error(e.getMessage());
        Error error = Error.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(error.getStatus(), error.getCode(), error.getMessage());
        return new ResponseEntity<>(errorResponse, error.getStatus());
    }
}

package GraduateOk.graduateokv2.exception;

import GraduateOk.graduateokv2.dto.common.ErrorResponse;
import GraduateOk.graduateokv2.security.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CustomExceptionHandler {

    private final JwtProvider jwtProvider;

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        Error error = e.getError();
        return new ResponseEntity<>(new ErrorResponse(error), error.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> ServerExceptionHandler(Exception e) {
        log.error(e.getMessage());
        Error error = Error.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(error), error.getStatus());
    }

    // security handler
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException(HttpServletRequest httpServletRequest, AccessDeniedException e) {
        try {
            String jwtToken = jwtProvider.getJwtToken(httpServletRequest);
            jwtProvider.validateToken(jwtToken);
        } catch (ExpiredJwtException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(new ErrorResponse(Error.EXPIRED_TOKEN), Error.EXPIRED_TOKEN.getStatus());
        } catch (Exception ignored) {
        }
        return new ResponseEntity<>(new ErrorResponse(Error.UNAUTHORIZED), Error.UNAUTHORIZED.getStatus());
    }
}

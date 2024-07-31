package GraduateOk.graduateokv2.exception;

import GraduateOk.graduateokv2.dto.common.ErrorResponse;
import GraduateOk.graduateokv2.security.JwtProvider;
import GraduateOk.graduateokv2.util.SlackMessageGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
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
    private final SlackMessageGenerator slackMessageGenerator;

    @Value("${SLACK_WEBHOOK_URL}")
    private String slackWebhookUrl;

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customExceptionHandler(HttpServletRequest request, CustomException e) {
        Error error = e.getError();

        SlackApi slackApi = new SlackApi(slackWebhookUrl);
        slackApi.call(slackMessageGenerator.generate(request, e, error));

        return new ResponseEntity<>(new ErrorResponse(error), error.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> ServerExceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage());
        Error error = Error.INTERNAL_SERVER_ERROR;

        SlackApi slackApi = new SlackApi(slackWebhookUrl);
        slackApi.call(slackMessageGenerator.generate(request, e, error));

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

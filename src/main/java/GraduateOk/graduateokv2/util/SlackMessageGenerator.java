package GraduateOk.graduateokv2.util;

import GraduateOk.graduateokv2.exception.Error;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class SlackMessageGenerator {

    public SlackMessage generate(HttpServletRequest request, Exception e, Error error) {
        SlackAttachment attachment = new SlackAttachment();
        attachment.setFallback("Error");
        attachment.setTitle("[" + error.getCode() + "] " + error.getMessage());
        attachment.setText(getErrorMessage(e) + "\n\n" + Arrays.toString(e.getStackTrace()));
        attachment.setFields(
                List.of(
                        new SlackField().setTitle("Request URL").setValue(getOriginalUrl(request)),
                        new SlackField().setTitle("Request Method").setValue(request.getMethod()),
                        new SlackField().setTitle("Request Time").setValue(new Date().toString()),
                        new SlackField().setTitle("Request IP").setValue(request.getHeader("X-Forwarded-For")),
                        new SlackField().setTitle("Request User-Agent").setValue(request.getHeader("User-Agent")))
        );

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setText(":exclamation:ERROR:exclamation:");
        slackMessage.setAttachments(Collections.singletonList(attachment));

        return slackMessage;
    }

    private String getErrorMessage(Exception e) {
        return StringUtils.hasText(e.getMessage()) ? e.getMessage() : "";
    }

    private String getOriginalUrl(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null) {
            scheme = request.getScheme();
        }

        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) {
            host = request.getHeader("Host");
        }

        String forwardedUri = request.getHeader("X-Forwarded-Uri");
        String requestUri = (forwardedUri != null) ? forwardedUri : request.getRequestURI();

        String originalUrl = scheme + "://" + host + requestUri;

        if (request.getQueryString() != null) {
            originalUrl += "?" + request.getQueryString();
        }

        return originalUrl;
    }
}

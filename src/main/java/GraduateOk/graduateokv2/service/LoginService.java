package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginService {
    public Long getLoginUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(
                Optional.ofNullable(authentication)
                        .orElseThrow(() -> new CustomException(Error.FORBIDDEN)).getClass())) {
            return Long.valueOf(authentication.getName());
        } else {
            throw new CustomException(Error.UNAUTHORIZED); // 로그인 필요
        }
    }
}

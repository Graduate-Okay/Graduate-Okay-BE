package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Review;
import GraduateOk.graduateokv2.domain.User;
import GraduateOk.graduateokv2.dto.user.UserRequest;
import GraduateOk.graduateokv2.dto.user.UserResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.ReviewRepository;
import GraduateOk.graduateokv2.repository.UserRepository;
import GraduateOk.graduateokv2.security.JwtProvider;
import GraduateOk.graduateokv2.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;
    private final LoginService loginService;
    private final ReviewRepository reviewRepository;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse.Join join(UserRequest.Join request) {
        // 이메일 중복 확인
        checkEmail(request.getEmail());

        // 비밀번호 길이 확인
        if (request.getPassword().length() < 8) {
            throw new CustomException(Error.BAD_PASSWORD);
        }

        // 비밀번호 암호화
        String encPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encPassword)
                .nickname(request.getNickname())
                .build();
        userRepository.save(user);

        return UserResponse.Join.builder().id(user.getId()).build();
    }

    /**
     * 이메일 중복 확인
     */
    private void checkEmail(String email) {
        userRepository.getUserByEmail(email).ifPresent(user -> {
            throw new CustomException(Error.ALREADY_SAVED_EMAIL);
        });
    }

    /**
     * 이메일 인증번호 발송
     */
    public void sendEmail(UserRequest.Email request) {
        String email = request.getEmail();

        // 이메일 중복 확인
        checkEmail(email);

        // 한신대 계정 확인
        if (!email.substring(email.indexOf('@')).equals("@hs.ac.kr")) {
            throw new CustomException(Error.BAD_EMAIL);
        }

        // 인증번호 생성
        String key = createKey();

        // 이메일 발송
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("졸업가능 이메일 인증번호");
            mimeMessageHelper.setText("졸업가능 이메일 인증번호 : " + key, true);
            javaMailSender.send(mimeMessage);

            // redis 저장
            redisUtil.setDateExpire(key, email, 300000L); // 제한시간 5분

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 인증번호 생성
     */
    private String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            key.append((rnd.nextInt(10)));
        }

        return key.toString();
    }

    /**
     * 이메일 인증번호 검증
     */
    public void verifyEmail(String number) {
        String email = redisUtil.getData(number);

        if (email == null) {
            throw new CustomException(Error.BAD_AUTH_NUMBER);
        }

        redisUtil.deleteData(number);
    }

    /**
     * 로그인
     */
    @Transactional
    public UserResponse.Login login(UserRequest.Login request) {
        User user = userRepository.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(Error.INVALID_PASSWORD);
        }

        UserResponse.Token tokenInfo = jwtProvider.generateToken(user);
        user.changeJwt(tokenInfo.getRefreshToken());

        return UserResponse.Login.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .tokenInfo(tokenInfo)
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout() {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));
        user.setJwtNull();
    }

    /**
     * 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public UserResponse.Info getUserInfo() {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        List<Review> reviewList = reviewRepository.getReviewByUser(user);

        return UserResponse.Info.of(user, reviewList);
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public UserResponse.UpdateInfo updateUserInfo(UserRequest.Update request) {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        if (request.getPassword() != null) {
            // 비밀번호 길이 확인
            if (request.getPassword().length() < 8) {
                throw new CustomException(Error.BAD_PASSWORD);
            }
            String encPassword = passwordEncoder.encode(request.getPassword());
            user.changePassword(encPassword);
        }

        if (request.getNickname() != null) {
            user.changeNickname(request.getNickname());
        }

        return UserResponse.UpdateInfo.of(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdraw() {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        // 회원이 작성한 리뷰 삭제
        List<Review> reviewList = reviewRepository.getReviewByUser(user);
        reviewRepository.deleteAll(reviewList);

        // 회원 삭제
        userRepository.delete(user);
    }
}

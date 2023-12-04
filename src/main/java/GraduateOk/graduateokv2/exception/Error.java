package GraduateOk.graduateokv2.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Error {

    // 400 BAD_REQUEST 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다."),
    BAD_PASSWORD(HttpStatus.BAD_REQUEST, "400_PW", "비밀번호는 8자 이상이어야 합니다."),
    BAD_EMAIL(HttpStatus.BAD_REQUEST, "400_EMAIL", "한신대학교 이메일이어야 합니다."),
    BAD_AUTH_NUMBER(HttpStatus.BAD_REQUEST, "400_AUTH", "잘못된 인증번호입니다."),
    BAD_PDF(HttpStatus.BAD_REQUEST, "400_PDF", "한신대학교 학업성적확인서 PDF여야 합니다."),
    INCORRECT_FILE_TYPE(HttpStatus.BAD_REQUEST, "400_FILE_TYPE", "잘못된 파일 형식입니다."),
    CANNOT_READ_PDF(HttpStatus.BAD_REQUEST, "400_READ_PDF", "파일을 읽을 수 없습니다."),

    // 401 UNAUTHORIZED 권한없음(인증 실패)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "권한 인증에 실패했습니다."),

    // 403 FORBIDDEN 권한없음(인가 실패)
    FORBIDDEN(HttpStatus.FORBIDDEN, "403", "권한이 없습니다."),
    INVALID_PASSWORD(HttpStatus.FORBIDDEN, "403_PASSWORD", "올바르지 않은 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "403_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "403_TOKEN_EXPIRED", "만료된 토큰입니다."),

    // 404 NOT_FOUND 잘못된 리소스 접근
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404_USER", "존재하지 않는 회원입니다."),
    NOT_FOUND_ADMIN(HttpStatus.NOT_FOUND, "404_ADMIN", "존재하지 않는 관리자입니다."),
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "404_NOTICE", "존재하지 않는 공지사항입니다."),
    NOT_FOUND_COLLEGE(HttpStatus.NOT_FOUND, "404_COLLEGE", "존재하지 않는 대학입니다."),
    NOT_FOUND_MAJOR(HttpStatus.NOT_FOUND, "404_MAJOR", "존재하지 않는 전공입니다."),
    NOT_FOUND_SUBJECT(HttpStatus.NOT_FOUND, "404_SUBJECT", "존재하지 않는 과목입니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "404_REVIEW", "존재하지 않는 리뷰입니다."),
    NOT_FOUND_PDF_CONTENT(HttpStatus.NOT_FOUND, "404_PDF_CONTENT", "PDF 내용이 존재하지 않습니다."),

    // 409 CONFLICT 중복된 리소스
    ALREADY_SAVED_EMAIL(HttpStatus.CONFLICT, "409_EMAIL", "이미 가입된 이메일입니다."),
    ALREADY_SAVED_LOGIN_ID(HttpStatus.CONFLICT, "409_ID", "이미 가입된 아이디입니다."),

    // 500 INTERNAL_SERVER_ERROR 서버 내부 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 내부 에러입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

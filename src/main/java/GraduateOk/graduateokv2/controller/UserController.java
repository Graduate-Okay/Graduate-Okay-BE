package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.dto.user.UserRequest;
import GraduateOk.graduateokv2.dto.user.UserResponse;
import GraduateOk.graduateokv2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * NAME : 회원가입
     * DATE : 2023-10-25
     */
    @PostMapping("/join")
    public BaseResponse<UserResponse.Join> join(@Valid @RequestBody UserRequest.Join request) {
        return BaseResponse.ok(HttpStatus.CREATED, userService.join(request));
    }

    /**
     * NAME : 이메일 인증번호 발송
     * DATE : 2023-10-25
     */
    @PostMapping("/email")
    public BaseResponse<?> sendEmail(@Valid @RequestBody UserRequest.Email request) {
        userService.sendEmail(request);
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 이메일 인증번호 검증
     * DATE : 2023-10-25
     */
    @GetMapping("/email")
    public BaseResponse<?> verifyEmail(@RequestParam String number) {
        userService.verifyEmail(number);
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 로그인
     * DATE : 2023-10-25
     */
    @PostMapping("/login")
    public BaseResponse<UserResponse.Login> login(@Valid @RequestBody UserRequest.Login request) {
        return BaseResponse.ok(HttpStatus.OK, userService.login(request));
    }

    /**
     * NAME : 로그아웃
     * DATE : 2023-10-25
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<?> logout() {
        userService.logout();
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 회원 정보 조회
     * DATE : 2023-10-25
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<UserResponse.Info> getUserInfo() {
        return BaseResponse.ok(HttpStatus.OK, userService.getUserInfo());
    }

    /**
     * NAME : 회원 정보 수정
     * DATE : 2023-10-25
     */
    @PatchMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<UserResponse.UpdateInfo> updateUserInfo(@Valid @RequestBody UserRequest.Update request) {
        return BaseResponse.ok(HttpStatus.OK, userService.updateUserInfo(request));
    }

    /**
     * NAME : 회원 탈퇴
     * DATE : 2023-10-25
     */
    @PostMapping("/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<?> withdraw() {
        userService.withdraw();
        return BaseResponse.ok(HttpStatus.OK);
    }
}

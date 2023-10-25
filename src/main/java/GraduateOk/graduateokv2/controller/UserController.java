package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.user.UserRequest;
import GraduateOk.graduateokv2.dto.user.UserResponse;
import GraduateOk.graduateokv2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse.Join> join(@Valid @RequestBody UserRequest.Join request) {
        return ResponseEntity.ok(userService.join(request));
    }

    /**
     * NAME : 이메일 인증번호 발송
     * DATE : 2023-10-25
     * todo: response 수정
     */
    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody UserRequest.Email request) {
        return ResponseEntity.ok(userService.sendEmail(request));
    }

    /**
     * NAME : 이메일 인증번호 검증
     * DATE : 2023-10-25
     * todo: response 수정
     */
    @GetMapping("/email")
    public ResponseEntity<String> verifyEmail(@RequestParam String number) {
        return ResponseEntity.ok(userService.verifyEmail(number));
    }

    /**
     * NAME : 로그인
     * DATE : 2023-10-25
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse.Login> login(@Valid @RequestBody UserRequest.Login request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * NAME : 로그아웃
     * DATE : 2023-10-25
     * todo: response 수정
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(userService.logout());
    }

    /**
     * NAME : 회원 정보 조회
     * DATE : 2023-10-25
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse.Info> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    /**
     * NAME : 회원 정보 수정
     * DATE : 2023-10-25
     */
    @PatchMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse.UpdateInfo> updateUserInfo(@Valid @RequestBody UserRequest.Update request) {
        return ResponseEntity.ok(userService.updateUserInfo(request));
    }

    /**
     * NAME : 회원 탈퇴
     * DATE : 2023-10-25
     * todo: response 수정
     */
    @PostMapping("/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> withdraw() {
        return ResponseEntity.ok(userService.withdraw());
    }
}

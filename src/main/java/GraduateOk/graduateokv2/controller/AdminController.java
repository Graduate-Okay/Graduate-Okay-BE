package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.admin.AdminRequest;
import GraduateOk.graduateokv2.dto.admin.AdminResponse;
import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * NAME : 관리자 등록 (관리자가 관리자 등록 가능)
     * DATE : 2023-10-26
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<AdminResponse.Register> registerAdmin(@Valid @RequestBody AdminRequest.Basic request) {
        return BaseResponse.ok(HttpStatus.OK, adminService.registerAdmin(request));
    }

    /**
     * NAME : 관리자 로그인
     * DATE : 2023-10-26
     */
    @PostMapping("/login")
    public BaseResponse<AdminResponse.Login> loginAdmin(@Valid @RequestBody AdminRequest.Basic request) {
        return BaseResponse.ok(HttpStatus.OK, adminService.loginAdmin(request));
    }

    /**
     * NAME : 관리자 로그아웃
     * DATE : 2023-10-26
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> logoutAdmin() {
        adminService.logoutAdmin();
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 관리자 비밀번호 변경
     * DATE : 2023-10-26
     */
    @PatchMapping("/password")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> updatePassword(@Valid @RequestBody AdminRequest.Password request) {
        adminService.updatePassword(request);
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 관리자 삭제
     * DATE : 2023-10-26
     */
    @DeleteMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteAdmin() {
        adminService.deleteAdmin();
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 관리자 목록 조회
     * DATE : 2023-10-26
     */
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<AdminResponse.AdminList> getAdminList(@PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                                                              Pageable pageable) {
        return BaseResponse.ok(HttpStatus.OK, adminService.getAdminList(pageable));
    }

    /**
     * NAME : 다른 관리자 삭제
     * DATE : 2023-10-26
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteAdmin(@PathVariable("id") Long id) {
        adminService.deleteAdmin(id);
        return BaseResponse.ok(HttpStatus.OK);
    }
}

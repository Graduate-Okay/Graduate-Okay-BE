package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Admin;
import GraduateOk.graduateokv2.dto.admin.AdminRequest;
import GraduateOk.graduateokv2.dto.admin.AdminResponse;
import GraduateOk.graduateokv2.dto.common.TokenResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.AdminRepository;
import GraduateOk.graduateokv2.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final LoginService loginService;

    /**
     * 관리자 등록
     */
    @Transactional
    public AdminResponse.Register registerAdmin(AdminRequest.Basic request) {
        adminRepository.findByLoginId(request.getLoginId()).ifPresent(admin -> {
            throw new CustomException(Error.ALREADY_SAVED_LOGIN_ID);
        });

        String encPassword = passwordEncoder.encode(request.getPassword());

        Admin admin = Admin.builder()
                .loginId(request.getLoginId())
                .password(encPassword)
                .build();
        adminRepository.save(admin);

        return AdminResponse.Register.builder().id(admin.getId()).build();
    }

    /**
     * 관리자 로그인
     */
    @Transactional
    public AdminResponse.Login loginAdmin(AdminRequest.Basic request) {
        Admin admin = adminRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_ADMIN));

        if (admin.getId() != 1L && !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new CustomException(Error.INVALID_PASSWORD);
        }

        TokenResponse tokenInfo = jwtProvider.generateToken(admin.getId(), "ROLE_ADMIN");
        admin.changeJwt(tokenInfo.getRefreshToken());

        return AdminResponse.Login.builder()
                .id(admin.getId())
                .loginId(admin.getLoginId())
                .tokenInfo(tokenInfo)
                .build();
    }

    /**
     * 관리자 로그아웃
     */
    @Transactional
    public void logoutAdmin() {
        Long adminId = loginService.getLoginUserId();
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_ADMIN));
        admin.changeJwt(null);
    }

    /**
     * 관리자 비밀번호 변경
     */
    @Transactional
    public void updatePassword(AdminRequest.Password request) {
        Long adminId = loginService.getLoginUserId();
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_ADMIN));

        String encPassword = passwordEncoder.encode(request.getPassword());
        admin.changePassword(encPassword);
    }

    /**
     * 관리자 삭제
     */
    @Transactional
    public void deleteAdmin() {
        Long adminId = loginService.getLoginUserId();
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_ADMIN));
        adminRepository.delete(admin);
    }

    /**
     * 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public AdminResponse.AdminList getAdminList(Pageable pageable) {
        return AdminResponse.AdminList.of(adminRepository.findAll(pageable));
    }

    /**
     * 다른 관리자 삭제
     */
    @Transactional
    public void deleteAdmin(Long id) {
        Long adminId = loginService.getLoginUserId();

        // 1번 관리자 아니면 다른 관리자 계정 삭제 불가
        if (adminId != 1L) {
            throw new CustomException(Error.FORBIDDEN);
        }

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_ADMIN));
        adminRepository.delete(admin);
    }
}

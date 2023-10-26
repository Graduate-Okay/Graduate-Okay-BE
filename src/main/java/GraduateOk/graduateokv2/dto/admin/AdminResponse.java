package GraduateOk.graduateokv2.dto.admin;

import GraduateOk.graduateokv2.domain.Admin;
import GraduateOk.graduateokv2.dto.common.TokenResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AdminResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Brief {

        Long id;

        String loginId;

        String createdAt; // 계정 등록일

        String updatedAt; // 마지막 로그인 일자

        public static List<Brief> of(List<Admin> adminList) {
            return adminList.stream()
                    .map(Brief::of)
                    .collect(Collectors.toList());
        }

        public static Brief of(Admin admin) {
            return Brief.builder()
                    .id(admin.getId())
                    .loginId(admin.getLoginId())
                    .createdAt(admin.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .updatedAt(admin.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AdminList {

        long totalCount;

        long maxPageCount;

        List<AdminResponse.Brief> adminList;

        public static AdminList of(Page<Admin> adminPage) {
            return AdminList.builder()
                    .totalCount(adminPage.getTotalElements())
                    .maxPageCount(adminPage.getTotalPages())
                    .adminList(AdminResponse.Brief.of(adminPage.getContent()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Register {

        Long id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {

        Long id;

        String loginId;

        TokenResponse tokenInfo;
    }
}

package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.dto.graduate.GraduateResponseDto;
import GraduateOk.graduateokv2.service.GraduateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/graduate-ok")
//@PreAuthorize("isAuthenticated()")
public class GraduateController {

    private final GraduateService graduateService;

    /**
     * NAME : 학업성적확인서 PDF로 졸업요건 검사
     * DATE : 2023-11-05
     */
    @PostMapping("")
    public BaseResponse<GraduateResponseDto> getGraduateOkResult(@RequestPart(name = "file") MultipartFile multipartFile) {
        return BaseResponse.ok(HttpStatus.OK, graduateService.isGraduateOk(multipartFile));
    }

    /**
     * NAME : 파일 업로드 테스트 API
     * DATE : 2023-12-04
     */
    @PostMapping("/test")
    public BaseResponse<String> testPdf(@RequestPart(name = "file") MultipartFile multipartFile) {
        return BaseResponse.ok(HttpStatus.OK, graduateService.extractPdfContent(multipartFile));
    }
}

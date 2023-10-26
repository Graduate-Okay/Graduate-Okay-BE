package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.dto.notice.NoticeRequest;
import GraduateOk.graduateokv2.dto.notice.NoticeResponse;
import GraduateOk.graduateokv2.service.NoticeService;
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
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * NAME : 공지사항 목록 조회
     * DATE : 2023-10-18
     */
    @GetMapping("")
    public BaseResponse<NoticeResponse.InfoList> getNoticeList(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                               Pageable pageable) {
        return BaseResponse.ok(HttpStatus.OK, noticeService.getNoticeList(pageable));
    }

    /**
     * NAME : 공지사항 상세 조회
     * DATE : 2023-10-17
     */
    @GetMapping("/{id}")
    public BaseResponse<NoticeResponse.Detail> getNoticeDetail(@PathVariable("id") Long id) {
        return BaseResponse.ok(HttpStatus.OK, noticeService.getNoticeDetail(id));
    }

    /**
     * NAME : 공지사항 작성 (관리자)
     * DATE : 2023-10-18
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<NoticeResponse.Register> registerNotice(@Valid @RequestBody NoticeRequest.Register request) {
        return BaseResponse.ok(HttpStatus.CREATED, noticeService.registerNotice(request));
    }

    /**
     * NAME : 공지사항 수정 (관리자)
     * DATE : 2023-10-18
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<NoticeResponse.Brief> updateNotice(@PathVariable("id") Long id,
                                                           @Valid @RequestBody NoticeRequest.Update request) {
        return BaseResponse.ok(HttpStatus.OK, noticeService.updateNotice(id, request));
    }

    /**
     * NAME : 공지사항 삭제 (관리자)
     * DATE : 2023-10-18
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteNotice(@PathVariable("id") Long id) {
        noticeService.deleteNotice(id);
        return BaseResponse.ok(HttpStatus.OK);
    }
}

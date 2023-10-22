package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.notice.NoticeRequest;
import GraduateOk.graduateokv2.dto.notice.NoticeResponse;
import GraduateOk.graduateokv2.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public NoticeResponse.InfoList getNoticeList(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return noticeService.getNoticeList(pageable);
    }

    /**
     * NAME : 공지사항 상세 조회
     * DATE : 2023-10-17
     */
    @GetMapping("/{id}")
    public NoticeResponse.Detail getNoticeDetail(@PathVariable("id") Long id) {
        return noticeService.getNoticeDetail(id);
    }

    /**
     * NAME : 공지사항 작성 (관리자)
     * DATE : 2023-10-18
     * todo: 권한 추가
     */
    @PostMapping("")
    public NoticeResponse.Register registerNotice(@Valid @RequestBody NoticeRequest.Register request) {
        return noticeService.registerNotice(request);
    }

    /**
     * NAME : 공지사항 수정 (관리자)
     * DATE : 2023-10-18
     * todo: 권한 추가
     */
    @PatchMapping("/{id}")
    public NoticeResponse.Brief updateNotice(@PathVariable("id") Long id,
                                             @Valid @RequestBody NoticeRequest.Update request) {
        return noticeService.updateNotice(id, request);
    }

    /**
     * NAME : 공지사항 삭제 (관리자)
     * DATE : 2023-10-18
     * todo: 권한 추가
     */
    @DeleteMapping("/{id}")
    public void deleteNotice(@PathVariable("id") Long id) {
        noticeService.deleteNotice(id);
    }
}

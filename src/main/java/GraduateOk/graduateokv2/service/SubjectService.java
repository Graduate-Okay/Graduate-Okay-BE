package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Subject;
import GraduateOk.graduateokv2.dto.subject.SubjectResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ReviewService reviewService;

    @Transactional(readOnly = true)
    public SubjectResponse.Rank getSubjectRank(String keyword, String type, Integer credit, Pageable pageable) {
        Page<Subject> subjectList = subjectRepository.getSubjectRank(keyword, type, credit, pageable);
        return SubjectResponse.Rank.of(subjectList);
    }

    @Transactional(readOnly = true)
    public SubjectResponse.Detail getSubjectDetail(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_SUBJECT));
        return SubjectResponse.Detail.of(subject, reviewService.getReviewSummary(id));
    }
}

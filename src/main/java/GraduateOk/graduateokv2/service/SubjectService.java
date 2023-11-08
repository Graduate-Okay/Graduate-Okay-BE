package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Major;
import GraduateOk.graduateokv2.domain.Subject;
import GraduateOk.graduateokv2.domain.SubjectCoreType;
import GraduateOk.graduateokv2.domain.SubjectModelType;
import GraduateOk.graduateokv2.dto.subject.SubjectRequest;
import GraduateOk.graduateokv2.dto.subject.SubjectResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.MajorRepository;
import GraduateOk.graduateokv2.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ReviewService reviewService;
    private final MajorRepository majorRepository;

    /**
     * 인기 교양 추천순 목록 조회
     */
    @Transactional(readOnly = true)
    public SubjectResponse.Rank getSubjectRank(String searchWord, String type, Integer credit, Pageable pageable) {
        SubjectModelType modelType = null;
        SubjectCoreType coreType = null;

        if (type != null) {
            if (type.contains("지성인") || type.contains("창의인") || type.contains("평화인")) {
                modelType = SubjectModelType.valueOf(type);
            } else {
                coreType = SubjectCoreType.valueOf(type);
            }
        }

        Page<Subject> subjectList = subjectRepository.getSubjectRank(searchWord, modelType, coreType, credit, pageable);

        return SubjectResponse.Rank.of(subjectList);
    }

    /**
     * 인기 교양 상세 조회
     */
    @Transactional(readOnly = true)
    public SubjectResponse.Detail getSubjectDetail(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_SUBJECT));
        return SubjectResponse.Detail.of(subject, reviewService.getReviewSummary(id));
    }

    /**------------------------------------------------------------------------------------------------------------*/

    /**
     * 과목 데이터(json) DB에 저장
     */
    @Transactional
    public SubjectResponse.Store storeSubjectData(SubjectRequest.Store request) {
        int count = 0;

        for (SubjectRequest.Data data : request.getDataList()) {
            String code = removeNonAlphaNumeric(data.getCourseCode());

            // 과목명 또는 과목 코드로 이미 저장했는지 확인
            Optional<Subject> subject = subjectRepository.findByNameOrCode(data.getCourseName(), code);

            // 같은 과목 코드인데 과목명이 다르면 subName에 새로운 과목명 저장
            subject.ifPresent(savedSubject -> {
                if (!savedSubject.getName().equals(data.getCourseName())) {
                    savedSubject.setSubName(data.getCourseName());
                }
            });

            if (subject.isEmpty()) { // 과목이 저장 안 되어 있으면 새로 저장
                // 교필, 교선, 전필 아니면 저장 안 함
                String classification = data.getClassification();
                if (classification.equals("전공선택")
                        || classification.equals("일반선택")
                        || classification.equals("공통선택")
                        || classification.equals("학석연계")
                        || classification.equals("평생교육")
                        || classification.equals("복수선택")
                        || classification.equals("연계전공")
                        || classification.equals("교직")) {
                    continue;
                }

                // 전공 없으면 새로 생성
                String majorName = data.getMajor().strip();
                String majorCode = code.substring(0, 2);
                Major major = majorRepository.findByName(majorName)
                        .orElseGet(() -> majorRepository.save(Major.builder()
                                .name(majorName)
                                .code(majorCode)
                                .build()));

                // 인재상 있을 경우 enum으로 변경
                SubjectModelType modelType = null;
                SubjectCoreType coreType = null;
                if (data.getKyType() != null && !data.getKyType().isEmpty()) {
                    String kyType = data.getKyType();
                    // 인재상, 핵심역량 구분
                    if (kyType.contains("하는")) {
                        modelType = SubjectModelType.descriptionToSubjectModelType(kyType);
                    } else {
                        coreType = SubjectCoreType.descriptionToSubjectCoreType(kyType);
                    }
                }

                // 과목 저장
                // todo: json 데이터에는 인재상만 있어서 핵심역량은 엑셀표 기준으로 추가 저장 필요
                subjectRepository.save(Subject.builder()
                        .name(data.getCourseName())
                        .code(code)
                        .classification(classification)
                        .isRequired(data.getClassification().contains("필수"))
                        .credit(getCreditFromString(data.getCredit()))
                        .kyModelType(modelType)
                        .kyCoreType(coreType)
                        .major(major)
                        .build()
                );
                count++;
            }
        }

        return SubjectResponse.Store.builder().totalCount(count).build();
    }

    private String removeNonAlphaNumeric(String input) {
        return input.replaceAll("[^A-Z0-9]", "");
    }

    private Float getCreditFromString(String creditStr) {
        if (creditStr.contains("(")) {
            return (float) 0;
        } else if (creditStr.contains(".")) {
            return 0.5F;
        } else {
            return Float.parseFloat(creditStr);
        }
    }

    /**------------------------------------------------------------------------------------------------------------*/

    /**
     * 과목 관리
     */
}
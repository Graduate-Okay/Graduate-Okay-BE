package GraduateOk.graduateokv2.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubjectType {

    // 인재상
    TALENTED_MODEL_INTELLIGENCE("소통하는 지성인"),
    TALENTED_MODEL_ORIGINALITY("도전하는 창의인"),
    TALENTED_MODEL_PEACE("실천하는 평화인"),

    // 핵심역량
    CORE_CAPABILITY_HUMANITIES("인문"),
    CORE_CAPABILITY_COMMUNICATION("소통"),
    CORE_CAPABILITY_INFORMATION("정보"),
    CORE_CAPABILITY_FUSION("창의융합"),
    CORE_CAPABILITY_GLOBAL("글로벌"),
    CORE_CAPABILITY_LEADERSHIP("리더십");

    private final String description;
}

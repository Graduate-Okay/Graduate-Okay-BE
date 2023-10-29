package GraduateOk.graduateokv2.domain;

import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SubjectCoreType {

    // 핵심역량
    CORE_CAPABILITY_HUMANITIES("인문"),
    CORE_CAPABILITY_COMMUNICATION("소통"),
    CORE_CAPABILITY_INFORMATION("정보"),
    CORE_CAPABILITY_FUSION("창의융합"),
    CORE_CAPABILITY_GLOBAL("글로벌"),
    CORE_CAPABILITY_LEADERSHIP("리더십");

    private final String description;

    private static final Map<String, String> STYLE_MAP = Collections.unmodifiableMap(
            Arrays.stream(values()).collect(Collectors.toMap(SubjectCoreType::getDescription, SubjectCoreType::name))
    );

    public static SubjectCoreType descriptionToSubjectCoreType(String description) {
        String name = STYLE_MAP.get(description);
        if (name == null) {
            throw new CustomException(Error.BAD_REQUEST);
        }
        return SubjectCoreType.valueOf(name);
    }
}

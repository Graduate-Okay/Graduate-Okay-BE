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
    BASIC("기초"),
    HUMANITIES("인문"),
    COMMUNICATION("소통"),
    INFORMATION("지식정보"),
    FUSION("창의융합"),
    GLOBAL("글로벌"),
    LEADERSHIP("리더십");

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

package GraduateOk.graduateokv2.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String loginId;

    String password;

    String jwt; // jwt refresh token

    public void changeJwt(String jwt) {
        this.jwt = jwt;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}

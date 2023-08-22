package com.zipkimi.user.dto.request;

import com.zipkimi.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Setter
public class UserLoginRequest {

    private String email;
    private String password;

    public UserEntity toUser(PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
    }

}

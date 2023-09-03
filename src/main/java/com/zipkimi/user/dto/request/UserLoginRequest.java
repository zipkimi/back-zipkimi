package com.zipkimi.user.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserLoginRequest {

    @NotBlank(message="이메일을 입력해주세요.")
    @Email(message = "이메일을 정확히 입력해주세요.")
    private String email;

    @NotBlank(message="비밀번호를 입력해주세요.")
    @Size(min=8, max=16, message = "8~16자 이내로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수문자 조합으로 8 ~ 16자리까지 가능합니다.")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}

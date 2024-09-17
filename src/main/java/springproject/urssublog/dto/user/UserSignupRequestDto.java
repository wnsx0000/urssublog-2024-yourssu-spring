package springproject.urssublog.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSignupRequestDto {
    @NotBlank(message = "email이 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    @Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z0-9.-]+$", message = "이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "password가 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String password;

    @NotBlank(message = "username이 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String username;
}

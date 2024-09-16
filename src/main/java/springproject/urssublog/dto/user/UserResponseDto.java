package springproject.urssublog.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String email;
    private String username;

    public UserResponseDto(String email, String username) {
        this.email = email;
        this.username = username;
    }
}

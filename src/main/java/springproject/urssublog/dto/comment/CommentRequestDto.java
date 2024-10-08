package springproject.urssublog.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "content가 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String content;
}

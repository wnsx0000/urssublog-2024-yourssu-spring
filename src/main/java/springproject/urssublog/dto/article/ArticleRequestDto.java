package springproject.urssublog.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ArticleRequestDto {
    @NotBlank(message = "content가 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String content;

    @NotBlank(message = "title이 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String title;
}

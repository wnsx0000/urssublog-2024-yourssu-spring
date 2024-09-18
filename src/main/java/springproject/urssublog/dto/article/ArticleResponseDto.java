package springproject.urssublog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ArticleResponseDto {
    private Long articleId;
    private String email;
    private String title;
    private String content;

    @Override
    public String toString() {
        return "-----------------------------------------------" +
                "\narticleId : " + articleId.toString() +
                "\nemail : " + email +
                "\ntitle : " + title +
                "\ncontent : " + content +
                "\n-----------------------------------------------";
    }
}

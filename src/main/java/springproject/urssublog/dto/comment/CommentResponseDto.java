package springproject.urssublog.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String email;
    private String content;

    @Override
    public String toString() {
        return "-----------------------------------------------" +
                "\ncommentId : " + commentId.toString() +
                "\nemail : " + email +
                "\ncontent : " + content +
                "\n-----------------------------------------------";
    }
}

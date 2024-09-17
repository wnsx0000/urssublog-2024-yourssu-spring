package springproject.urssublog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdTime;
    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "content가 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Comment(String content) {
        this.content = content;
    }
}

package springproject.urssublog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article")
@Getter
@Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdTime;
    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "content가 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String content;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "title이 비어있을 수 없습니다.")
    @Size(max = 255, message = "문자열 최대 길이는 255자 입니다.")
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Article(String content, String title) {
        this.content = content;
        this.title = title;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}

package springproject.urssublog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdTime;
    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "email이 비어있을 수 없습니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "password가 비어있을 수 없습니다.")
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "username이 비어있을 수 없습니다.")
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addArticle(Article article) {
        articles.add(article);
    }
}

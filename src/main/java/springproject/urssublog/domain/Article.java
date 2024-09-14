package springproject.urssublog.domain;

import jakarta.persistence.*;
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

    @Column(name = "created_at")
    private LocalDateTime createdTime;
    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    private String content;
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();
}

package springproject.urssublog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.User;

import java.util.Optional;

public interface JpaArticleRepository extends JpaRepository<Article, Long> {
}

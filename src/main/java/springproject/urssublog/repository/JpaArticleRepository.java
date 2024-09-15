package springproject.urssublog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.User;

public interface JpaArticleRepository extends JpaRepository<Article, Long> {
}

package springproject.urssublog.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.Comment;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ArticleRepositoryTest {
    @Autowired
    JpaArticleRepository articleRepository;

    /**
     * Comment 엔티티 db 등록 테스트 : 성공한 경우.
     */
    @Test
    public void saveCommentSuccess() {
        //given
        Article article = new Article("title~", "hello my name is jhun");
        article.setCreatedTime(LocalDateTime.now());

        //when
        articleRepository.save(article);

        //then
        log.debug("saveCommentSuccess(), user.getId()={}", article.getId());
        assertThat(articleRepository.findById(article.getId()).isPresent()).isTrue();
    }
}

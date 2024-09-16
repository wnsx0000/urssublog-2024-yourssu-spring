package springproject.urssublog.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ArticleRepositoryTest {
    @Autowired
    JpaArticleRepository articleRepository;
    @Autowired
    JpaCommentRepository commentRepository;

    /**
     * Article 엔티티 db 등록 테스트 : 성공한 경우.
     */
    @Test
    public void saveArticleSuccess() {
        //given
        Article article = new Article("title~", "hello my name is jhun");
        article.setCreatedTime(LocalDateTime.now());

        //when
        articleRepository.save(article);

        //then
        log.debug("saveArticleSuccess(), user.getId()={}", article.getId());
        assertThat(articleRepository.findById(article.getId()).isPresent()).isTrue();
    }

    /**
     * Article 엔티티 db 등록 테스트 : 게시글 등록 시 title, content의 길이가 너무 긴 경우.
     * DataIntegrityViolationException로 처리하는 경우에 대한 테스트 코드.
     * DataIntegrityViolationException 대신 MethodArgumentNotValidException(검증 예외)로 처리하는 것으로 수정했다.
     */
//    @Test
//    public void saveArticleTooLongFailure() {
//        //given
//        String s = "";
//        for (int i = 0; i < 10; i++) { // 총 300자
//            s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//        }
//        log.debug("saveArticleTooLongFailure(), length of s={}", s.length());
//
//        //when
//        Article article1 = new Article(s, "title");
//        article1.setCreatedTime(LocalDateTime.now());
//        Article article2 = new Article("content", s);
//        article2.setCreatedTime(LocalDateTime.now());
//
//        //then
//        DataIntegrityViolationException thrown1 = assertThrows(DataIntegrityViolationException.class, () -> {
//            articleRepository.save(article1);
//        });
//        log.debug("saveArticleTooLongFailure(), user1(too long content) exception message='{}'", thrown1.getMessage());
//        DataIntegrityViolationException thrown2 = assertThrows(DataIntegrityViolationException.class, () -> {
//            articleRepository.save(article2);
//        });
//        log.debug("saveArticleTooLongFailure(), user2(too long title) exception message='{}'", thrown2.getMessage());
//    }

    /**
     * Article 엔티티 db 수정 테스트 : 성공한 경우.
     */
    @Test
    public void updateArticleSuccess() {
        //given
        Article article = new Article("title~", "hello my name is jhun");
        article.setCreatedTime(LocalDateTime.now());
        articleRepository.save(article);

        //when
        article.setTitle("new title");
        article.setContent("new content");

        //then
        Article newArticle = articleRepository.findById(article.getId()).get();
        assertThat(newArticle.getTitle()).isEqualTo("new title");
        assertThat(newArticle.getContent()).isEqualTo("new content");
    }

    /**
     * Article 엔티티 db 수정 테스트 : 게시글 수정 시 title, content의 길이가 너무 긴 경우.
     * DataIntegrityViolationException로 처리하는 경우에 대한 테스트 코드.
     * DataIntegrityViolationException 대신 MethodArgumentNotValidException(검증 예외)로 처리하는 것으로 수정했다.
     */
//    @Test
//    public void updateArticleTooLongFailure() {
//        //given
//        Article article = new Article("title~", "hello my name is jhun");
//        article.setCreatedTime(LocalDateTime.now());
//        articleRepository.save(article);
//
//        String s = "";
//        for (int i = 0; i < 10; i++) { // 총 300자
//            s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//        }
//        log.debug("updateArticleTooLongFailure(), length of s={}", s.length());
//        String longString = s;
//
//        //when, then
//        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class, () -> {
//            article.setContent(longString);
//        });
//        log.debug("updateArticleTooLongFailure(), user1(too long content) exception message='{}'", thrown.getMessage());
//        thrown = assertThrows(DataIntegrityViolationException.class, () -> {
//            article.setTitle(longString);
//        });
//        log.debug("updateArticleTooLongFailure(), user1(too long content) exception message='{}'", thrown.getMessage());
//    }

    /**
     * Article 엔티티 db 삭제 테스트 : 성공한 경우. (댓글 삭제도 확인.)
     */
    @Test
    public void deleteArticleSuccess() {
        //given
        Comment comment = new Comment("hello");
        comment.setCreatedTime(LocalDateTime.now());

        Article article = new Article("title~", "hello my name is jhun");
        article.setCreatedTime(LocalDateTime.now());
        article.addComment(comment);
        articleRepository.save(article);

        //when
        articleRepository.deleteById(article.getId());

        //then
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isFalse();
        assertThat(articleRepository.findById(article.getId()).isPresent()).isFalse();
    }
}

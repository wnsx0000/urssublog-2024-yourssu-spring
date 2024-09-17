package springproject.urssublog.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.classes.BlogResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
public class ArticleServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;

    /**
     * Article service 계층 게시물 등록 테스트 : 성공한 경우.
     */
    @Test
    public void articleSaveSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");

        //when
        Long id = articleService.saveArticle(article, user.getId());

        //then
        assertThat(articleService.getTitleById(id)).isEqualTo("title~~");
        assertThat(article.getUser().getArticles().contains(article)).isTrue();
    }

    /**
     * Article service 계층 게시물 수정 테스트 : 성공한 경우.
     */
    @Test
    public void articleUpdateSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());

        //when
        Article newArticle = new Article("newContent", "newTitle");
        newArticle.setId(article.getId());
        articleService.updateArticle(newArticle);

        //then
        assertThat(articleService.getTitleById(article.getId())).isEqualTo("newTitle");
    }

    /**
     * Article service 계층 게시물 수정 테스트 : 게시글 수정 시 해당 id의 게시글이 존재하지 않는 경우.
     * → BlogResourceNotFoundException
     */
    @Test
    public void articleUpdateNotFoundFailure() {
        //given
        Article article = new Article("content~~", "title~~");
        article.setId(10L);

        //when, then
        BlogResourceNotFoundException thrown = assertThrows(BlogResourceNotFoundException.class, () -> {
            articleService.updateArticle(article);
        });
        log.debug("articleUpdateNotFoundFailure(), no article to update exception message={}", thrown.getMessage());

    }

    /**
     * Article service 계층 게시물 삭제 테스트 : 성공한 경우.
     */
    @Test
    public void articleDeleteSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());
        Long id = article.getId();

        //when
        articleService.deleteArticle(id);

        //then
        assertThat(articleService.getTitleById(id)).isNull();
        assertThat(user.getArticles().contains(article)).isFalse();
    }

    /**
     * Article service 계층 게시물 삭제 테스트 : 게시글 삭제 시 해당 id의 게시글이 존재하지 않는 경우.
     * → BlogResourceNotFoundException
     */
    @Test
    public void articleDeleteNotFoundFailure() {
        //given, when, then
        BlogResourceNotFoundException thrown = assertThrows(BlogResourceNotFoundException.class, () -> {
            articleService.deleteArticle(10L);
        });
        log.debug("articleDeleteNotFoundFailure(), no article to delete exception message={}", thrown.getMessage());
    }

}

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
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
public class CommentServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    /**
     * Comment service 계층 댓글 등록 테스트 : 성공한 경우.
     */
    @Test
    public void saveCommentSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());
        Comment comment = new Comment("comment content~");

        //when
        Long id = commentService.saveComment(comment, article.getId(), user.getId());

        //then
        assertThat(commentService.getContentById(id)).isEqualTo("comment content~");
        assertThat(comment.getArticle().getComments().contains(comment)).isTrue();
        assertThat(comment.getUser().getComments().contains(comment)).isTrue();
    }

    /**
     * Comment service 계층 댓글 수정 테스트 : 성공한 경우.
     */
    @Test
    public void updateCommentSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());
        Comment comment = new Comment("comment content~");
        Long commentId = commentService.saveComment(comment, article.getId(), user.getId());

        //when
        Comment newComment = new Comment("new comment...!");
        newComment.setId(commentId);
        commentService.updateComment(newComment);

        //then
        assertThat(commentService.getContentById(commentId)).isEqualTo("new comment...!");
    }

    /**
     * Comment service 계층 댓글 삭제 테스트 : 성공한 경우.
     */
    @Test
    public void deleteCommentSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());
        Comment comment = new Comment("comment content~");
        Long commentId = commentService.saveComment(comment, article.getId(), user.getId());

        //when
        commentService.deleteComment(commentId);

        //then
        assertThat(commentService.getContentById(commentId)).isNull();
    }
}

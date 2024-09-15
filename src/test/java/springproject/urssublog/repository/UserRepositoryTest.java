package springproject.urssublog.repository;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*; // assertj assertions
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class UserRepositoryTest {
    @Autowired JpaUserRepository userRepository;
    @Autowired JpaCommentRepository commentRepository;
    @Autowired JpaArticleRepository articleRepository;

    /**
     * User 엔티티 db 등록 테스트 : 성공한 경우.
     */
    @Test
    public void saveUserSucceed() {
        //given
        User user = new User("wnsx0000@gmail.com", "mypassword", "jhun");
        user.setCreatedTime(LocalDateTime.now());

        //when
        userRepository.save(user);

        //then
        log.debug("saveUserSuccess(), user.getId()={}", user.getId());
        assertThat(userRepository.findById(user.getId()).isPresent()).isTrue();
    }

    /**
     * User 엔티티 db 등록 테스트 : 회원가입 정보(email, username)가 겹치는 경우. → DataAccessException
     */
    @Test
    public void saveUserFailed1() {
        //given
        User user = new User("wnsx0000@gmail.com", "mypassword", "jhun");
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);

        //when
        User sameEmailUser = new User("wnsx0000@gmail.com", "mypassword", "diff");
        sameEmailUser.setCreatedTime(LocalDateTime.now());
        User sameNameUser = new User("diff@gmail.com", "mypassword", "jhun");
        sameNameUser.setCreatedTime(LocalDateTime.now());

        //then
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userRepository.save(sameEmailUser);
        });
        log.debug("saveUserFailed1(), sameEmailUser exception message={}", thrown.getMessage());

        DataAccessException thrown2 = assertThrows(DataAccessException.class, () -> {
            userRepository.save(sameNameUser);
        });
        log.debug("saveUserFailed1(), sameNameUser exception message={}", thrown2.getMessage());
    }

    /**
     * User 엔티티 db 등록 테스트 : 회원가입 정보(email, username, password)의 문자열이 너무 긴 경우. → DataAccessException
     */
    @Test
    public void saveUserFailed2() {
        //given
        String s = "";
        for (int i = 0; i < 10; i++) { // 총 300자
            s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        }
        log.debug("saveUserFailed2(), length of s={}", s.length());

        //when
        User user1 = new User("11@gmail.com" + s, "mypassword", "jhun");
        user1.setCreatedTime(LocalDateTime.now());
        User user2 = new User("wwws@gmail.com", s, "jh213n");
        user2.setCreatedTime(LocalDateTime.now());
        User user3 = new User("ws@gmail.com", "mypassword", s);
        user3.setCreatedTime(LocalDateTime.now());

        //then
        ConstraintViolationException thrown1 = assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(user1);
        });
        log.debug("saveUserFailed2, user1(too long email) exception message='{}'", thrown1.getMessage());
        DataAccessException thrown2 = assertThrows(DataAccessException.class, () -> {
            userRepository.save(user2);
        });
        log.debug("saveUserFailed2, user2(too long password) exception message='{}'", thrown2.getMessage());
        DataAccessException thrown3 = assertThrows(DataAccessException.class, () -> {
            userRepository.save(user3);
        });
        log.debug("saveUserFailed2, user3(too long username) exception message='{}'", thrown3.getMessage());
    }

    /**
     * User 엔티티 db 삭제 테스트 : 성공한 경우. (댓글, 게시물 삭제도 확인.)
     */
    @Test
    public void deleteUserSucceed() {
        //given
        Comment comment = new Comment("hello");
        comment.setCreatedTime(LocalDateTime.now());
        Article article = new Article("title~", "hello my name is jhun");
        article.setCreatedTime(LocalDateTime.now());

        User user = new User("wnsx0000@gmail.com", "mypassword", "jhun");
        user.setCreatedTime(LocalDateTime.now());
        user.addComment(comment);
        user.addArticle(article);
        userRepository.save(user);

        //when
        userRepository.deleteById(user.getId());

        //then
        assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isFalse();
        assertThat(articleRepository.findById(article.getId()).isPresent()).isFalse();
    }
}

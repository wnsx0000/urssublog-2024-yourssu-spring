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
import springproject.urssublog.domain.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class CommentRepositoryTest {
    @Autowired
    JpaCommentRepository commentRepository;

    /**
     * Comment 엔티티 db 등록 테스트 : 성공한 경우.
     */
    @Test
    public void saveCommentSuccess() {
        //given
        Comment comment = new Comment("hello");
        comment.setCreatedTime(LocalDateTime.now());

        //when
        commentRepository.save(comment);

        //then
        log.debug("saveCommentSuccess(), user.getId()={}", comment.getId());
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isTrue();
    }

    /**
     * Comment 엔티티 db 수정 테스트 : 성공한 경우.
     */
    @Test
    public void updateCommentSuccess() {
        //given
        Comment comment = new Comment("content!~~");
        comment.setCreatedTime(LocalDateTime.now());
        commentRepository.save(comment);

        //when
        comment.setContent("new content");

        //then
        Comment newComment = commentRepository.findById(comment.getId()).get();
        assertThat(newComment.getContent()).isEqualTo("new content");
    }

    /**
     * Comment 엔티티 db 삭제 테스트 : 성공한 경우.
     */
    @Test
    public void deleteCommentSuccess() {
        //given
        Comment comment = new Comment("hello");
        comment.setCreatedTime(LocalDateTime.now());
        commentRepository.save(comment);

        //when
        commentRepository.deleteById(comment.getId());

        //then
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isFalse();
    }
}

package springproject.urssublog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.classes.BlogResourceNotFoundException;
import springproject.urssublog.exception.classes.BlogUserNotFoundException;
import springproject.urssublog.repository.JpaArticleRepository;
import springproject.urssublog.repository.JpaCommentRepository;
import springproject.urssublog.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final JpaUserRepository userRepository;
    private final JpaArticleRepository articleRepository;
    private final JpaCommentRepository commentRepository;

    /**
     * content를 가지고 있는 Comment 객체와 게시물 id, 로그인 중인 사용자의 id를 파라미터로 받아 댓글을 등록한다.
     * 추가로, 생성 시간을 지정한다.
     * @author Jun Lee
     */
    @Transactional
    public Long saveComment(Comment comment, Long articleId, Long userId) {
        //article, user 가져오기
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if(optionalArticle.isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id의 게시글이 존재하지 않습니다.");
        }
        Article article = optionalArticle.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new BlogUserNotFoundException("해당 id의 회원이 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        //댓글 저장
        comment.setCreatedTime(LocalDateTime.now());
        comment.setUser(user);
        comment.setArticle(article);

        article.getComments().add(comment);
        user.getComments().add(comment);

        commentRepository.save(comment);

        return comment.getId();
    }

    /**
     * commentId, content를 가지고 있는 새로운 Comment 객체를 파라미터로 받아 해당 내용으로 댓글을 수정한다.
     * 추가로, 수정 시간을 지정한다.
     * @author Jun Lee
     */
    @Transactional
    public void updateComment(Comment newComment) {
        Optional<Comment> optionalComment = commentRepository.findById(newComment.getId());
        if(optionalComment.isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id의 게시물이 존재하지 않습니다.");
        }

        Comment comment = optionalComment.get();
        comment.setUpdatedTime(LocalDateTime.now());
        comment.setContent(newComment.getContent());
    }

    /**
     * 댓글 id를 파라미터로 받아 해당 댓글을 삭제한다.
     * @author Jun Lee
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(optionalComment.isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id의 게시물이 존재하지 않습니다.");
        }

        Comment comment = optionalComment.get();
        comment.getUser().getComments().remove(comment);
        comment.getArticle().getComments().remove(comment);

        commentRepository.deleteById(comment.getId());
    }

    // 디버깅용. Comment 객체의 content 조회 메서드
    public String getContentById(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        return (comment != null ? comment.getContent() : null);
    }
}

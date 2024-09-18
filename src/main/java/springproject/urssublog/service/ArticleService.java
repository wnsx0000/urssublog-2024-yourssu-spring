package springproject.urssublog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.classes.BlogResourceNotFoundException;
import springproject.urssublog.exception.classes.BlogUserNotFoundException;
import springproject.urssublog.repository.JpaArticleRepository;
import springproject.urssublog.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final JpaUserRepository userRepository;
    private final JpaArticleRepository articleRepository;

    /**
     * Article 객체와 로그인 중인 사용자의 id를 파라미터로 받아 게시글을 등록한다. 추가로, 생성 시간을 지정한다.
     * @author Jun Lee
     */
    @Transactional
    public Long saveArticle(Article article, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new BlogUserNotFoundException("해당 id의 회원이 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        article.setCreatedTime(LocalDateTime.now());
        article.setUser(user);
        user.getArticles().add(article);
        articleRepository.save(article);
        return article.getId();
    }

    /**
     * id, title, content를 가지고 있는 새로운 Article 객체를 파라미터로 받아 해당 내용으로 게시글을 수정한다.
     * 추가로, 수정 시간을 지정한다.
     * @author Jun Lee
     */
    @Transactional
    public void updateArticle(Article newArticle) {
        Optional<Article> optionalArticle = articleRepository.findById(newArticle.getId());
        if(optionalArticle.isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id의 게시글이 존재하지 않습니다.");
        }

        Article article = optionalArticle.get();
        article.setUpdatedTime(LocalDateTime.now());
        article.setTitle(newArticle.getTitle());
        article.setContent(newArticle.getContent());
    }

    /**
     * 게시글 id를 파라미터로 받아 해당 게시글을 삭제한다.
     * @author Jun Lee
     */
    @Transactional
    public void deleteArticle(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if(optionalArticle.isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id의 게시글이 존재하지 않습니다.");
        }

        Article article = optionalArticle.get();
        article.getUser().getArticles().remove(article);
        articleRepository.deleteById(article.getId());
    }

    // 디버깅용. Article 객체의 title 조회 메서드
    public String getTitleById(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        return (article != null ? article.getTitle() : null);
    }
}

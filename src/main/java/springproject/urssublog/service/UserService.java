package springproject.urssublog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.classes.BlogNotAuthorizedException;
import springproject.urssublog.exception.classes.BlogResourceNotFoundException;
import springproject.urssublog.exception.classes.BlogUserNotFoundException;
import springproject.urssublog.repository.JpaUserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository userRepository;

    /**
     * User 객체를 파라미터로 받아 회원가입을 수행한다. 추가로, 비밀번호 암호화, 생성 시간 지정을 수행한다.
     * @author Jun Lee
     */
    @Transactional
    public void saveUser(User user) {
        user.setPassword(encryptStringWithSha256(user.getPassword()));
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * user id를 파라미터로 받아 회원 탈퇴(삭제)를 수행한다.
     * @author Jun Lee
     */
    @Transactional
    public void deleteUser(Long userId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new BlogResourceNotFoundException("해당 id를 가진 사용자가 없습니다.");
        }
        userRepository.deleteById(userId);
    }

    /**
     * User 객체를 파라미터로 받아 로그인 성공 시 해당 객체를, 실패한 경우 예외를 던진다.
     * @author Jun Lee
     */
    public User userLoginCheck(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if(optionalUser.isPresent()
                && optionalUser.get().getPassword().equals(encryptStringWithSha256(user.getPassword()))) {
            return optionalUser.get();
        }
        else {
            throw new BlogUserNotFoundException("로그인 실패. 회원 정보가 없습니다.");
        }
    }

    // 암호화 메서드
    public String encryptStringWithSha256(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(s.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    // 디버깅용. User 객체의 username 조회 메서드
    public String getUsernameById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null ? user.getUsername() : null);
    }

    // 해당 id의 게시물이 지정한 id 사용자의 소유인지 판별
    public void checkIsArticleFromUser(Long articleId, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new BlogUserNotFoundException("해당 id의 회원이 존재하지 않습니다.");
        }
        if(optionalUser.get().getArticles().stream().noneMatch(
                article -> article.getId().equals(articleId))
        ) {
            throw new BlogNotAuthorizedException("로그인 중인 사용자의 게시글이 아닙니다.");
        }
    }

    // 해당 id의 댓글이 지정한 id 사용자의 소유인지 판별
    public void checkIsCommentFromUser(Long commentId, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new BlogUserNotFoundException("해당 id의 회원이 존재하지 않습니다.");
        }
        if(optionalUser.get().getComments().stream().noneMatch(
                comment -> comment.getId().equals(commentId)
        )) {
            throw new BlogNotAuthorizedException("로그인 중인 사용자의 댓글이 아닙니다.");
        }
    }
}

package springproject.urssublog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.BlogResourceNotFoundException;
import springproject.urssublog.exception.BlogUserNotFoundException;
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
     * User 객체를 파라미터로 받아 회원가입을 수행한다.
     * 이때 비밀번호 암호화, 생성 시간 지정을 수행한다.
     * @author Jun Lee
     */
    public void saveUser(User user) {
        user.setPassword(encryptStringWithSha256(user.getPassword()));
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * user id를 파라미터로 받아 회원 탈퇴(삭제)를 수행한다.
     * @author Jun Lee
     */
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

    // 디버깅용 User 객체의 username 조회 메서드
    public String getUsernameById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return null;
        }
        else {
            return user.getUsername();
        }
    }
}

package springproject.urssublog.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.User;
import springproject.urssublog.exception.BlogResourceNotFoundException;
import springproject.urssublog.exception.BlogUserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class UserServiceTest {
    @Autowired UserService userService;

    /**
     * 비밀번호 암호화 테스트 : 성공한 경우.
     */
    @Test
    public void passwordEncryptionTest() {
        //given, when
        String a = userService.encryptStringWithSha256("same");
        log.debug("passwordEncryptionTest(), 'same' is encrypted to {} in a", a);
        String b = userService.encryptStringWithSha256("same");
        log.debug("passwordEncryptionTest(), 'same' is encrypted to {} in b", b);
        String c = userService.encryptStringWithSha256("different");
        log.debug("passwordEncryptionTest(), 'different' is encrypted to {} in c", c);

        //then
        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    /**
     * User service 계층 회원가입 테스트 : 성공한 경우.
     */
    @Test
    public void userSignupSuccess() {
        //given
        User user = new User("email~", "password~", "username~");

        //when
        userService.saveUser(user);

        //then
        assertThat(userService.getUsernameById(user.getId())).isEqualTo("username~");

    }

    /**
     * User service 계층 회원 삭제 테스트 : 성공한 경우.
     */
    @Test
    public void userDeleteSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);
        Long userId = user.getId();

        //when
        userService.deleteUser(userId);

        //then
        assertThat(userService.getUsernameById(userId)).isNull();
    }

    /**
     * User service 계층 회원 삭제 테스트 : 회원 탈퇴(삭제) 시 해당 id의 회원이 존재하지 않는 경우. → BlogResourceNotFoundException
     */
    @Test
    public void userDeleteNotFoundFailure() {
        //given
        Long wrongId = 10L;

        //when, then
        BlogResourceNotFoundException thrown = assertThrows(BlogResourceNotFoundException.class, () -> {
            userService.deleteUser(wrongId);
        });
        log.debug("userDeleteNotFoundFailure(), no user to delete exception message={}", thrown.getMessage());
    }

    /**
     * User service 계층 회원 로그인 테스트 : 성공한 경우.
     */
    @Test
    public void userLoginSuccess() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);

        //when
        User newUser = new User("email~", "password~");
        User resultUser = userService.userLoginCheck(newUser);

        //then
        assertThat(resultUser).isEqualTo(user);
    }

    /**
     * User service 계층 회원 로그인 테스트 : 로그인 시 회원 정보가 없는 경우. → BlogResourceNotFoundException
     */
    @Test
    public void userLoginNotFoundFailure() {
        //given
        User user = new User("email~", "password~", "username~");
        userService.saveUser(user);

        //when
        User wrongEmailUser = new User("!!!!!", "password~");
        User wrongPasswordUser = new User("email~", "!!!!!");

        //then
        BlogUserNotFoundException  thrown1 = assertThrows(BlogUserNotFoundException .class, () -> {
            userService.userLoginCheck(wrongEmailUser);
        });
        log.debug("userLoginNotFoundFailure(), wrong login email exception message={}", thrown1.getMessage());
        BlogUserNotFoundException thrown2 = assertThrows(BlogUserNotFoundException .class, () -> {
            userService.userLoginCheck(wrongEmailUser);
        });
        log.debug("userLoginNotFoundFailure(), wrong login password exception message={}", thrown2.getMessage());

    }
}

package springproject.urssublog.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.User;

import static org.assertj.core.api.Assertions.assertThat;

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
}

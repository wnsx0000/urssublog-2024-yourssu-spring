package springproject.urssublog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.User;
import springproject.urssublog.dto.user.UserLoginRequestDto;
import springproject.urssublog.dto.user.UserResponseDto;
import springproject.urssublog.dto.user.UserSignupRequestDto;
import springproject.urssublog.repository.JpaUserRepository;
import springproject.urssublog.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService; // 테스트 데이터 설정용

    /**
     * /users POST 회원가입 테스트 : 성공한 경우.
     */
    @Test
    public void signupSuccess() throws Exception {
        //given
        UserSignupRequestDto requestDto
                = new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", "username~~");
        UserResponseDto responseDto
                = new UserResponseDto("wnsx0000@gmail.com", "username~~");

        //when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    /**
     * /users POST 회원가입 테스트 : 회원가입 정보(email, username)가 겹치는 경우. -> DataIntegrityViolationException
     */
    @Test
    public void signupDuplicatedFailure() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "pasword~~", "jhun");
        userService.saveUser(user);

        UserSignupRequestDto emailDuplicatedDto
                = new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", "diff");
        UserSignupRequestDto usernameDuplicatedDto
                = new UserSignupRequestDto("diff@gmail.com", "password~~", "jhun");

        //when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDuplicatedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameDuplicatedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /users POST 회원가입 테스트 : 회원가입 시 spring validation 검증(@NotBlank, @Size, @Pattern)에 위배된 경우. (400)
     * → MethodArgumentNotValidException
     */
    @Test
    public void signupNotValidFailure() throws Exception {
        //given
        List<UserSignupRequestDto> dtoList = new ArrayList<>();
        //blank
        dtoList.add(new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", ""));
        dtoList.add(new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", " "));
        dtoList.add(new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", null));
        //too long size
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new UserSignupRequestDto("wnsx0000@gmail.com", "password~~", longString));
        //wrong pattern
        dtoList.add(new UserSignupRequestDto("wrong pattern", "password~~", "diff"));
        //everything wrong
        dtoList.add(new UserSignupRequestDto("wrong pattern", " ", longString));

        //when, then
        for (UserSignupRequestDto userSignupRequestDto : dtoList) {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userSignupRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /users/login POST 로그인 테스트 : 성공한 경우.
     */
    @Test
    public void loginSuccess() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        UserLoginRequestDto requestDto
                = new UserLoginRequestDto("wnsx0000@gmail.com", "pasword~~");
        UserResponseDto responseDto
                = new UserResponseDto("wnsx0000@gmail.com", "username~~");

        //when, then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.request()
                        .sessionAttribute("username", "username~~"))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    /**
     * /users/login POST 로그인 테스트 : 로그인 시 회원 정보가 없는 경우.(404) → BlogUserNotFoundException
     */
    @Test
    public void loginNotFoundFailure() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "mypassword", "username~~");
        userService.saveUser(user);

        List<UserLoginRequestDto> dtoList = new ArrayList<>();
        dtoList.add(new UserLoginRequestDto("wnsx0000@gmail.com", "diff"));
        dtoList.add(new UserLoginRequestDto("diff@gmail.com", "mypassword"));

        //when, then
        for (UserLoginRequestDto userLoginRequestDto : dtoList) {
            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("404 NOT_FOUND"));
        }
    }

    /**
     * /users/login POST 로그인 테스트 : 로그인 시 spring validation 검증(@NotBlank, @Size, @Pattern)에 위배된 경우. (400)
     * → MethodArgumentNotValidException
     */
    @Test
    public void loginNotValidFailure() throws Exception {
        //given
        List<UserLoginRequestDto> dtoList = new ArrayList<>();
        //blank
        dtoList.add(new UserLoginRequestDto("wnsx0000@gmail.com", ""));
        dtoList.add(new UserLoginRequestDto("wnsx0000@gmail.com", " "));
        dtoList.add(new UserLoginRequestDto("wnsx0000@gmail.com", null));
        //too long size
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new UserLoginRequestDto("wnsx0000@gmail.com", longString));
        //wrong pattern
        dtoList.add(new UserLoginRequestDto("wrong pattern", "password~~"));
        //everything wrong
        dtoList.add(new UserLoginRequestDto("wrong pattern", " "));

        //when, then
        for (UserLoginRequestDto userLoginRequestDto : dtoList) {
            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /users/logout GET 로그아웃 테스트 : 성공한 경우.
     */
    @Test
    public void logoutSuccess() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        mockMvc.perform(get("/users/logout")
                        .session(session))
                .andExpect(status().isNoContent());
    }

    /**
     * /users/{userId} DELETE 회원 탈퇴(삭제) 테스트 : 성공한 경우.
     */
    @Test
    public void deleteUserSuccess() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        log.debug("deleteUser(), DELETE method to url={}", "/users/" + user.getId());

        mockMvc.perform(delete("/users/" + user.getId())
                        .session(session))
                .andExpect(status().isNoContent());
    }

    /**
     * /users/{userId} DELETE 회원 탈퇴(삭제) 테스트 : 회원 탈퇴(삭제) 시 해당 리소스가 로그인 중인 회원의 리소스가 아닌 경우.
     * (해당 id의 회원이 존재하지 않는 경우도 처리됨.)
     * → BlogNotAuthorizedException
     */
    @Test
    public void deleteUserNotLoginUserFailure() throws Exception {
        //given
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        log.debug("deleteUserNotLoginUserFailure(), userId={}", user.getId());
        log.debug("deleteUserNotLoginUserFailure(), DELETE method to url={}", "/users/" + (user.getId() + 1));

        mockMvc.perform(delete("/users/" + (user.getId() + 1))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }
}

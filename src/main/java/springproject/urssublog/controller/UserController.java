package springproject.urssublog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springproject.urssublog.domain.User;
import springproject.urssublog.dto.comment.CommentResponseDto;
import springproject.urssublog.dto.user.UserLoginRequestDto;
import springproject.urssublog.dto.user.UserResponseDto;
import springproject.urssublog.dto.user.UserSignupRequestDto;
import springproject.urssublog.exception.classes.BlogNotAuthorizedException;
import springproject.urssublog.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원가입
     * @author Jun Lee
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto signup(
            @Valid @RequestBody UserSignupRequestDto requestDto
    ) {
        User user = new User(requestDto.getEmail(), requestDto.getPassword(), requestDto.getUsername());
        userService.saveUser(user);

        UserResponseDto responseDto = new UserResponseDto(requestDto.getEmail(), requestDto.getUsername());
        log.debug("UserResponseDto, POST method to /users\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 로그인
     * @author Jun Lee
     */
    @PostMapping("/users/login")
    public UserResponseDto login(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletRequest request
            ) {
        User inputUser = new User(requestDto.getEmail(), requestDto.getPassword());
        User user = userService.userLoginCheck(inputUser);

        // session 추가
        HttpSession session = request.getSession(true);
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        UserResponseDto responseDto = new UserResponseDto(user.getEmail(), user.getUsername());
        log.debug("UserResponseDto, POST method to /users/login\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 로그아웃
     * @author Jun Lee
     */
    @GetMapping("/users/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        session.invalidate();
    }

    /**
     * 회원 탈퇴(삭제)
     * @author Jun Lee
     */
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable("userId") String userId,
            HttpServletRequest request
    ) {
        log.debug("UserController, deleteUser() mapped to /users/{userId} called.");

        HttpSession session = request.getSession(false);
        if(!session.getAttribute("id").equals(Long.parseLong(userId))) {
            throw new BlogNotAuthorizedException("다른 사용자의 계정은 삭제할 수 없습니다.");
        }
        userService.deleteUser(Long.parseLong(userId));
    }
}

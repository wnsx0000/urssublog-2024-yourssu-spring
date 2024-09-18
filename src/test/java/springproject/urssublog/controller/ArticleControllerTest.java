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
import org.springframework.transaction.annotation.Transactional;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.User;
import springproject.urssublog.dto.article.ArticleRequestDto;
import springproject.urssublog.dto.article.ArticleResponseDto;
import springproject.urssublog.dto.user.UserResponseDto;
import springproject.urssublog.dto.user.UserSignupRequestDto;
import springproject.urssublog.service.ArticleService;
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
public class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService; // 테스트 데이터 설정용
    @Autowired
    private ArticleService articleService; // 테스트 데이터 설정용

    /**
     * /posts POST 게시물 등록 테스트 : 성공한 경우. (201 Created)
     */
    @Test
    public void saveArticleSuccess() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        ArticleRequestDto requestDto = new ArticleRequestDto("content~~", "title~~");

        //when, then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("wnsx0000@gmail.com"))
                .andExpect(jsonPath("$.title").value("title~~"))
                .andExpect(jsonPath("$.content").value("content~~"));
    }

    /**
     * /posts POST 게시물 등록 테스트 : 게시글 등록 시 spring validation 검증(@NotBlank, @Size)에 위배된 경우. (400)
     * → MethodArgumentNotValidException
     */
    @Test void saveArticleNotValidFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        List<ArticleRequestDto> dtoList = new ArrayList<>();
        dtoList.add(new ArticleRequestDto("", "title~~"));
        dtoList.add(new ArticleRequestDto(" ", "title~~"));
        dtoList.add(new ArticleRequestDto(null, "title~~"));
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new ArticleRequestDto(longString, "title~~"));

        //when, then
        for (ArticleRequestDto requestDto : dtoList) {
            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /posts/{articleId} PUT 게시물 수정 테스트 : 성공한 경우.
     */
    @Test void updateArticleSuccess() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //게시물 등록
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());

        ArticleRequestDto requestDto = new ArticleRequestDto("new content", "new title");

        //when, then
        mockMvc.perform(put("/posts/" + article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId().toString()))
                .andExpect(jsonPath("$.email").value("wnsx0000@gmail.com"))
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.content").value("new content"));
    }

    /**
     * /posts/{articleId} PUT 게시물 수정 테스트 : 게시글 수정 시 spring validation 검증(@NotBlank, @Size)에 위배된 경우. (400)
     * → MethodArgumentNotValidException
     */
    @Test void updateArticleNotValidFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //게시물 등록
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());

        List<ArticleRequestDto> dtoList = new ArrayList<>();
        dtoList.add(new ArticleRequestDto("", "title~~"));
        dtoList.add(new ArticleRequestDto(" ", "title~~"));
        dtoList.add(new ArticleRequestDto(null, "title~~"));
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new ArticleRequestDto(longString, "title~~"));

        //when, then
        for (ArticleRequestDto requestDto : dtoList) {
            mockMvc.perform(put("/posts/" + article.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /posts/{articleId} DELETE 게시물 수정 테스트 : 게시글 수정 시 해당 리소스가 로그인 중인 회원의 리소스가 아닌 경우. (400)
     * → BlogNotAuthorizedException
     */
    @Test void updateArticleNoAuthorFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //게시물 등록
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());

        ArticleRequestDto requestDto = new ArticleRequestDto("new content", "new title");

        mockMvc.perform(put("/posts/" + (article.getId() + 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /posts/{articleId} DELETE 게시물 삭제 테스트 : 성공한 경우. (204 No Content)
     */
    @Test void deleteArticleSuccess() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //게시물 등록
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, user.getId());

        //when, then
        mockMvc.perform(delete("/posts/" + article.getId())
                        .session(session))
                .andExpect(status().isNoContent());
    }

    /**
     * /posts/{articleId} DELETE 게시물 삭제 테스트 : 게시글 삭제 시 해당 리소스가 로그인 중인 회원의 리소스가 아닌 경우. (400)
     * → BlogNotAuthorizedException
     */
    @Test void deleteArticleNoAuthorFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);
        User anotherUser = new User("anothoer@gmail.com", "dfdfdf", "anothoer");
        userService.saveUser(anotherUser);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //게시물 등록
        Article article = new Article("content~~", "title~~");
        articleService.saveArticle(article, anotherUser.getId());

        mockMvc.perform(delete("/posts/" + article.getId())
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }
}

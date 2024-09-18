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
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;
import springproject.urssublog.dto.article.ArticleRequestDto;
import springproject.urssublog.dto.comment.CommentRequestDto;
import springproject.urssublog.service.ArticleService;
import springproject.urssublog.service.CommentService;
import springproject.urssublog.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService; // 테스트 데이터 설정용
    @Autowired
    private ArticleService articleService; // 테스트 데이터 설정용
    @Autowired
    private CommentService commentService; // 테스트 데이터 설정용

    /**
     * /posts/{articleId}/comments POST 댓글 등록 테스트 : 성공
     */
    @Test
    public void saveCommentSuccess() throws Exception {
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

        //요청 dto 구성
        CommentRequestDto requestDto = new CommentRequestDto("content of comment");

        //when, then
        log.debug("saveCommentSuccess(), json -> '{}' by objectMapper", objectMapper.writeValueAsString(requestDto));
        mockMvc.perform(post("/posts/" + article.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("wnsx0000@gmail.com"))
                .andExpect(jsonPath("$.content").value("content of comment"));
    }

    /**
     * /posts/{articleId}/comments POST 댓글 등록 테스트 : 댓글 등록 시 해당 리소스(게시글)가 로그인 중인 회원의 리소스가 아닌 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void saveCommentNoAuthorFailure() throws Exception {
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

        //when, then
        CommentRequestDto requestDto = new CommentRequestDto("mycomment");
        mockMvc.perform(post("/posts/" + article.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /posts/{articleId}/comments POST 댓글 등록 테스트 : 댓글 등록 시 해당 id의 리소스(게시글)가 존재하지 않는 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void saveCommentNotFoundFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        CommentRequestDto requestDto = new CommentRequestDto("mycomment");
        mockMvc.perform(post("/posts/" + 10L + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /posts/{articleId}/comments POST 댓글 등록 테스트 : 댓글 등록 시 spring validation 검증(@NotBlank, @Size)에 위배된 경우.
     * → MethodArgumentNotValidException
     */
    @Test
    public void saveCommentNotValidFailure() throws Exception {
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

        //dto 작성
        List<CommentRequestDto> dtoList = new ArrayList<>();
        dtoList.add(new CommentRequestDto(""));
        dtoList.add(new CommentRequestDto(" "));
        dtoList.add(new CommentRequestDto(null));
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new CommentRequestDto(longString));
        //when, then
        for (CommentRequestDto requestDto : dtoList) {
            mockMvc.perform(post("/posts/" + article.getId() + "/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /comments/{commentId} PUT 댓글 수정 테스트 : 성공
     */
    @Test
    public void updateCommentSuccess() throws Exception {
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

        //댓글 등록
        Comment comment = new Comment("content of comment");
        commentService.saveComment(comment, article.getId(), user.getId());

        //when, then
        CommentRequestDto requestDto = new CommentRequestDto("new content");
        mockMvc.perform(put("/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId().toString()))
                .andExpect(jsonPath("$.email").value("wnsx0000@gmail.com"))
                .andExpect(jsonPath("$.content").value("new content"));
    }

    /**
     * /comments/{commentId} PUT 댓글 수정 테스트 : 댓글 수정 시 해당 리소스가 로그인 중인 회원의 리소스가 아닌 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void updateCommentNoAuthorFailure() throws Exception {
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
        articleService.saveArticle(article, user.getId());

        //댓글 등록
        Comment comment = new Comment("content of comment");
        commentService.saveComment(comment, article.getId(), anotherUser.getId());

        //when, then
        CommentRequestDto requestDto = new CommentRequestDto("new content");
        mockMvc.perform(put("/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /comments/{commentId} PUT 댓글 수정 테스트 : 댓글 수정 시 해당 id의 리소스가 존재하지 않는 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void updateCommentNotFoundFailure() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        CommentRequestDto requestDto = new CommentRequestDto("new content");
        mockMvc.perform(put("/comments/" + 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /comments/{commentId} PUT 댓글 수정 테스트 : 댓글 수정 시 spring validation 검증(@NotBlank, @Size)에 위배된 경우.
     * → MethodArgumentNotValidException
     */
    @Test
    public void updateCommentNotValidFailure() throws Exception {
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

        //댓글 등록
        Comment comment = new Comment("content of comment");
        commentService.saveComment(comment, article.getId(), user.getId());

        //dto 작성
        List<CommentRequestDto> dtoList = new ArrayList<>();
        dtoList.add(new CommentRequestDto(""));
        dtoList.add(new CommentRequestDto(" "));
        dtoList.add(new CommentRequestDto(null));
        String base = "a";
        String longString = base.repeat(300);
        dtoList.add(new CommentRequestDto(longString));
        //when, then
        for (CommentRequestDto requestDto : dtoList) {
            mockMvc.perform(put("/comments/" + comment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
        }
    }

    /**
     * /comments/{commentId} DELETE 댓글 삭제 테스트 : 성공
     */
    @Test
    public void deleteCommentSuccess() throws Exception {
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

        //댓글 등록
        Comment comment = new Comment("content of comment");
        commentService.saveComment(comment, article.getId(), user.getId());

        //when, then
        mockMvc.perform(delete("/comments/" + comment.getId())
                        .session(session))
                .andExpect(status().isNoContent());
    }

    /**
     * /comments/{commentId} DELETE 댓글 삭제 테스트 : 댓글 삭제 시 해당 리소스(게시글)가 로그인 중인 회원의 리소스가 아닌 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void deleteCommentNoAuthorFailed() throws Exception {
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
        articleService.saveArticle(article, user.getId());

        //댓글 등록
        Comment comment = new Comment("content of comment");
        commentService.saveComment(comment, article.getId(), anotherUser.getId());

        //when, then
        mockMvc.perform(delete("/comments/" + comment.getId())
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }

    /**
     * /comments/{commentId} DELETE 댓글 삭제 테스트 : 댓글 삭제 시 해당 id의 리소스가 존재하지 않는 경우.
     * → BlogNotAuthorizedException
     */
    @Test
    public void deleteCommentNotFoundFailed() throws Exception {
        //given
        //회원가입
        User user = new User("wnsx0000@gmail.com", "pasword~~", "username~~");
        userService.saveUser(user);

        //세션 생성(로그인)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        //when, then
        mockMvc.perform(delete("/comments/" + 10L)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }
}

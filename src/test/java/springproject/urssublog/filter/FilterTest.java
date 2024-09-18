package springproject.urssublog.filter;

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
import springproject.urssublog.domain.User;
import springproject.urssublog.dto.article.ArticleRequestDto;
import springproject.urssublog.dto.comment.CommentRequestDto;
import springproject.urssublog.dto.user.UserResponseDto;
import springproject.urssublog.dto.user.UserSignupRequestDto;
import springproject.urssublog.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FilterTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    /**
     * login check filter 테스트 : 로그인 중이 아닌 경우
     */
    @Test
    public void loginCheckFilterNoLoginTest() throws Exception {
        //given, when, then
        CommentRequestDto requestDto = new CommentRequestDto("mycomment");
        mockMvc.perform(post("/posts/" + 10L + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
    }


}

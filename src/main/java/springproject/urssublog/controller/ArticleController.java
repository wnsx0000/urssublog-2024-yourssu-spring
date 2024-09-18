package springproject.urssublog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springproject.urssublog.domain.Article;
import springproject.urssublog.dto.article.ArticleRequestDto;
import springproject.urssublog.dto.article.ArticleResponseDto;
import springproject.urssublog.exception.classes.BlogNotAuthorizedException;
import springproject.urssublog.service.ArticleService;
import springproject.urssublog.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final UserService userService;
    private final ArticleService articleService;

    /**
     * 게시글 작성
     * @author Jun Lee
     */
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseDto saveArticle(
            @Valid @RequestBody ArticleRequestDto requestDto,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");
        String email = (String)session.getAttribute("email");

        Article article = new Article(requestDto.getContent(), requestDto.getTitle());
        Long articleId = articleService.saveArticle(article, userId);

        ArticleResponseDto responseDto = new ArticleResponseDto(articleId, email, article.getTitle(), article.getContent());
        log.debug("ArticleResponseDto, POST method to /posts\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 게시글 수정
     * @author Jun Lee
     */
    @PutMapping("/posts/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponseDto updateArticle(
            @Valid @RequestBody ArticleRequestDto requestDto,
                @PathVariable("articleId") Long articleId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");
        String email = (String)session.getAttribute("email");

        userService.checkIsArticleFromUser(articleId, userId);

        Article newArticle = new Article(requestDto.getContent(), requestDto.getTitle());
        newArticle.setId(articleId);
        articleService.updateArticle(newArticle);

        ArticleResponseDto responseDto = new ArticleResponseDto(articleId, email, newArticle.getTitle(), newArticle.getContent());
        log.debug("ArticleResponseDto, PUT method to /posts/{articleId}\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 게시글 삭제
     * @author Jun Lee
     */
    @DeleteMapping("/posts/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(
            @PathVariable("articleId") Long articleId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");

        userService.checkIsArticleFromUser(articleId, userId);

        articleService.deleteArticle(articleId);
    }
}

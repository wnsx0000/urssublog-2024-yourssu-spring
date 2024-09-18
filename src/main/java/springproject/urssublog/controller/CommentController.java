package springproject.urssublog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springproject.urssublog.domain.Article;
import springproject.urssublog.domain.Comment;
import springproject.urssublog.dto.article.ArticleRequestDto;
import springproject.urssublog.dto.article.ArticleResponseDto;
import springproject.urssublog.dto.comment.CommentRequestDto;
import springproject.urssublog.dto.comment.CommentResponseDto;
import springproject.urssublog.service.CommentService;
import springproject.urssublog.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final UserService userService;
    private final CommentService commentService;

    /**
     * 댓글 작성
     * @author Jun Lee
     */
    @PostMapping("/posts/{articleId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto saveComment(
            @Valid @RequestBody CommentRequestDto requestDto,
            @PathVariable("articleId") Long articleId,
            HttpServletRequest request
    ) {
        //세션에서 값 불러오기
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");
        String email = (String)session.getAttribute("email");

        //해당 게시글이 현재 로그인 중인 사용자의 리소스인지 확인
        userService.checkIsArticleFromUser(articleId, userId);

        //댓글 저장
        Comment comment = new Comment(requestDto.getContent());
        Long commentId = commentService.saveComment(comment, articleId, userId);

        //응답 작성
        CommentResponseDto responseDto = new CommentResponseDto(commentId, email, comment.getContent());
        log.debug("CommentResponseDto, POST method to /posts/{articleId}/comments\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 댓글 수정
     * @author Jun Lee
     */
    @PutMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(
            @Valid @RequestBody CommentRequestDto requestDto,
            @PathVariable("commentId") Long commentId,
            HttpServletRequest request
    ) {
        //세션에서 값 불러오기
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");
        String email = (String)session.getAttribute("email");

        //해당 댓글이 현재 로그인 중인 사용자의 리소스인지 확인
        userService.checkIsCommentFromUser(commentId, userId);

        //댓글 수정
        Comment newComment = new Comment(requestDto.getContent());
        newComment.setId(commentId);
        commentService.updateComment(newComment);

        //응답 작성
        CommentResponseDto responseDto = new CommentResponseDto(commentId, email, newComment.getContent());
        log.debug("CommentResponseDto, PUT method to /comments/{commentId}\n{}", responseDto.toString());
        return responseDto;
    }

    /**
     * 댓글 삭제
     * @author Jun Lee
     */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable("commentId") Long commentId,
            HttpServletRequest request
    ) {
        //세션에서 값 불러오기
        HttpSession session = request.getSession(false);
        Long userId = (Long)session.getAttribute("id");

        //해당 댓글이 현재 로그인 중인 사용자의 리소스인지 확인
        userService.checkIsCommentFromUser(commentId, userId);

        //댓글 삭제
        commentService.deleteComment(commentId);
    }
}

package springproject.urssublog.exception.handling;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springproject.urssublog.dto.exception.ExceptionResponseDto;
import springproject.urssublog.exception.classes.BlogNotAuthorizedException;
import springproject.urssublog.exception.classes.BlogResourceNotFoundException;
import springproject.urssublog.exception.classes.BlogUserNotFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice

public class UnitedExceptionHandlingClass {
    /**
     * 회원가입 정보(email, username)가 겹치는 경우에 발생하는 DataIntegrityViolationException 처리.
     * @author Jun Lee
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handleDataIntegrityViolationException(
            DataIntegrityViolationException e,
            HttpServletRequest request
    ) {
        ExceptionResponseDto responseClass = new ExceptionResponseDto();
        responseClass.setTime(LocalDateTime.now().toString());
        responseClass.setStatus(HttpStatus.BAD_REQUEST.toString());
        responseClass.setMessage("데이터베이스 무결성 위반. 중복된 email 또는 username을 사용했을 수 있습니다.");
        responseClass.setRequestURI(request.getRequestURI());

        log.debug("UnitedExceptionHandlingClass, Exception response.\n{}", responseClass.toString());

        return responseClass;
    }

    /**
     * spring validation에서의 검증에 위배된 경우에 발생하는 MethodArgumentNotValidException 처리.
     * @author Jun Lee
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        StringBuilder rst = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> {
            rst.append(error.getDefaultMessage());
        });

        ExceptionResponseDto responseClass = new ExceptionResponseDto();
        responseClass.setTime(LocalDateTime.now().toString());
        responseClass.setStatus(HttpStatus.BAD_REQUEST.toString());
        responseClass.setMessage(rst.toString());
        responseClass.setRequestURI(request.getRequestURI());

        log.debug("UnitedExceptionHandlingClass, Exception response.\n{}", responseClass.toString());

        return responseClass;
    }

    /**
     * 로그인 등의 상황에서 해당 사용자가 존재하지 않는 경우에 발생하는 BlogUserNotFoundException 처리.
     * @author Jun Lee
     */
    @ExceptionHandler(BlogUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDto handleBlogUserNotFoundException(
            BlogUserNotFoundException e,
            HttpServletRequest request
    ) {
        ExceptionResponseDto responseClass = new ExceptionResponseDto();
        responseClass.setTime(LocalDateTime.now().toString());
        responseClass.setStatus(HttpStatus.NOT_FOUND.toString());
        responseClass.setMessage(e.getMessage());
        responseClass.setRequestURI(request.getRequestURI());

        log.debug("UnitedExceptionHandlingClass, Exception response.\n{}", responseClass.toString());

        return responseClass;
    }


    /**
     * 리소스의 수정, 삭제 시에 해당 리소스가 존재하지 않는 경우에 발생하는 BlogResourceNotFoundException 처리.
     * @author Jun Lee
     */
    @ExceptionHandler(BlogResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDto handleBlogResourceNotFoundException(
            BlogResourceNotFoundException e,
            HttpServletRequest request
    ) {
        ExceptionResponseDto responseClass = new ExceptionResponseDto();
        responseClass.setTime(LocalDateTime.now().toString());
        responseClass.setStatus(HttpStatus.NOT_FOUND.toString());
        responseClass.setMessage(e.getMessage());
        responseClass.setRequestURI(request.getRequestURI());

        log.debug("UnitedExceptionHandlingClass, Exception response.\n{}", responseClass.toString());

        return responseClass;
    }

    /**
     * 리소스의 수정, 삭제 시에 다른 사용자의 리소스에 접근하려고 하는 경우에 발생하는 BlogNotAuthorizedException 처리.
     * @author Jun Lee
     */
    @ExceptionHandler(BlogNotAuthorizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDto handleBlogNotAuthorizedException(
            BlogNotAuthorizedException e,
            HttpServletRequest request
    ) {
        ExceptionResponseDto responseClass = new ExceptionResponseDto();
        responseClass.setTime(LocalDateTime.now().toString());
        responseClass.setStatus(HttpStatus.BAD_REQUEST.toString());
        responseClass.setMessage(e.getMessage());
        responseClass.setRequestURI(request.getRequestURI());

        log.debug("UnitedExceptionHandlingClass, Exception response.\n{}", responseClass.toString());

        return responseClass;
    }
}

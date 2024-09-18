package springproject.urssublog.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import springproject.urssublog.exception.handling.ExceptionResponseClass;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class LoginCheckFilter implements Filter {
    private final String[] accessibleUri = {"/users", "/users/login"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();

        //로그인 중인지 검사
        if((!isAccessibleUri(requestUri)) && (request.getSession(false) == null)) {
            ExceptionResponseClass responseDto = new ExceptionResponseClass(
                    LocalDateTime.now(),
                    "400 BAD_REQUEST",
                    "로그인 중이 아닙니다.",
                    requestUri
            );
            log.debug("LoginCheckFilter, no login status\n{}", responseDto.toString());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAccessibleUri(String requestUri) {
        for(String s : accessibleUri) {
            if(s.equals(requestUri)) {
                return true;
            }
        }
        return false;
    }
}
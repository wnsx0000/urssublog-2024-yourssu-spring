package springproject.urssublog.filter;

import jakarta.servlet.*;

import java.io.IOException;
import java.util.Map;

public class AccessCheckFilter implements Filter {
    private Map<String, String> accessableUriAndMethod = Map.of(
            "", "",
            "", ""
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {



    }
}

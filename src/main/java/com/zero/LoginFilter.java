package com.zero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * @ClassName: LoginFilter
 * @Description: 登录过滤器
 */
@Order(1)
@Component("loginFilter")
public class LoginFilter implements Filter {

    private static Logger logger = LogManager.getLogger(LoginFilter.class);

    private static final String[] URL_MATCHES = {};

    /**
     * Title：doFilter
     * Description: 所有请求都走此过滤器来判断用户是否登录
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // 判断是否是http请求
        if (!(servletRequest instanceof HttpServletRequest)
                || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(
                    "OncePerRequestFilter just supports HTTP requests");
        }
        // 获得在下面代码中要用的request,response,session对象
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpRequest.getSession(false);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=utf-8");

        String referer = httpRequest.getHeader("referer");
        logger.info("referer: {}", referer);

        StringBuffer url = httpRequest.getRequestURL();

        if (url.indexOf("login") >= 0 || url.indexOf("auth") >= 0) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        

        // 从session中获取用户信息
        if (Objects.isNull(session) || Objects.isNull(SessionUtils.getCurrentUser(httpRequest))) {
            // 用户不存在,踢回登录页面
            logger.info("未登录，无权限访问！{}", url);

            ObjectMapper mapper = new ObjectMapper();
            Writer writer = httpResponse.getWriter();
            mapper.writeValue(writer, Result.resultAuth());
            writer.close();
            return;
        } else {
        	// 用户存在,可以访问此地址
        	filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void init(FilterConfig arg0) {
    }

    @Override
    public void destroy() {

    }


}

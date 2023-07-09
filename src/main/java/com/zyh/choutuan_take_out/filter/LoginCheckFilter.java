package com.zyh.choutuan_take_out.filter;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.reader.FieldReaderList;
import com.zyh.choutuan_take_out.common.BaseContext;
import com.zyh.choutuan_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /**
         * 1.获取uri
         * 2.判断是否拦截
         * 3.判断登录状态
         * 4.返回
         */
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/employee/page",
                "/backend/page/login/login.html",
                "/front/page/login.html",
                "/user/sendMsg",
                "/user/login"
        };
        String[] employeeUrls = new String[]{
                "/employee/**",
        };
        String[] userUrls = new String[]{
                "/front/page/**",
                "/shoppingCart/**"
        };
        if(checkRoute(urls, requestURI)){
            filterChain.doFilter(request, response);
            return;
        }
        Object employeeId = request.getSession().getAttribute("employeeId");
        if( employeeId == null && checkRoute(employeeUrls, requestURI)){
//            response.sendRedirect("/backend/page/login/login.html");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            log.info("拦截到请求:{}", request.getRequestURL());
            return;
        }
        if(employeeId != null){
            BaseContext.setCurrentId((Long)employeeId);
        }
        Object userId = request.getSession().getAttribute("userId");
        if( userId == null && checkRoute(userUrls, requestURI)){
//            response.sendRedirect("/front/page/login.html");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            log.info("拦截到请求:{}", request.getRequestURL());
            return ;
        }
        filterChain.doFilter(request, response);
        return ;
    }

    public boolean checkRoute(String[] uris, String requestUri){
        for (String uri : uris){
            boolean match = PATH_MATCHER.match(uri, requestUri);
            if(match){
                return true;
            }
        }
        return false;
    }
}

package org.example.handler;

import cn.hutool.json.JSONUtil;
import org.example.entity.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: zhaoxinguo
 * @Date: 2018/9/20 14:55
 * @Description: 自定义认证拦截器
 * @desc AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
        Result result = Result.error(401, authException.getMessage());
        String message = JSONUtil.toJsonStr(result);
        response.getWriter().write(message);
    }

}
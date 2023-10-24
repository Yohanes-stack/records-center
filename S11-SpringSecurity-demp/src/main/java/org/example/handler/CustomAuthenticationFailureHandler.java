package org.example.handler;

import com.alibaba.fastjson.JSON;
import org.example.constants.LoginResponseType;
import org.example.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理器
 * AuthenticationFailureHandler 用来解决身份验证失败的异常(适用表单登录方式)
 *
 * @author zhaoxg on 2023年04月18日 10:15
 */
@Component("customAuthenticationFailureHandler")
//public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Value("${loginType}")
    private String loginType;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, IOException, ServletException {
        // 认证失败状态码 401
        /*Result result = Result.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        String message = JSON.toJSONString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(message);*/
        if (LoginResponseType.JSON.equals(loginType)) {
            // 认证失败响应JSON字符串，
            Result result = Result.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
            String message = JSON.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(message);
        } else {
            // 重写向回认证页面，注意加上 ?error
            super.setDefaultFailureUrl("/login/page"+ "?error");
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
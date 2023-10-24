package org.example.handler;

import com.alibaba.fastjson.JSON;
import org.example.constants.LoginResponseType;
import org.example.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证成功处理器
 *
 * @author zhaoxg on 2023年04月18日 11:23
 */
@Component("customAuthenticationSuccessHandler")
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Value("${loginType}")
    private String loginType;

    /**
     * 认证成功后处理逻辑
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 当认证成功后，响应 JSON 数据给前端
        /*Result result = Result.ok("认证成功");
        String message = JSON.toJSONString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(message);*/
        if (LoginResponseType.JSON.equals(loginType)) {
            // 当认证成功后，响应 JSON 数据给前端
            Result result = Result.ok("认证成功");
            String message = JSON.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(message);
        } else {
            //重定向到上次请求的地址上，引发跳转到认证页面的地址
            logger.info("authentication: " + JSON.toJSONString(authentication));
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
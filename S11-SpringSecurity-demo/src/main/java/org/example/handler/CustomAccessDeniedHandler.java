package org.example.handler;

import cn.hutool.json.JSONUtil;
import org.example.entity.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        Result result = Result.error(403, e.getMessage());
        String message = JSONUtil.toJsonStr(result);
        response.getWriter().write(message);
    }
}

package org.example.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.constants.ConstantKey;
import org.example.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 登陆认证流程过滤器
 * Jwt登陆过滤器，验证账号密码正确，以及token生成，并返回
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    public JwtLoginFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //请求 /login 登陆时拦截
        super.doFilter(req, res, chain);
    }

    /**
     * 尝试身份认证（接受并解析用户凭证）
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 认证成功（用户成功登陆后，这个方法会被调用，内部生成token）
     * @param request
     * @param response
     * @param chain
     * @param auth
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String token = null;

        try {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            //获取到角色 xxx:xxx
            List<String> roleList = new ArrayList();

            for (GrantedAuthority authority : authorities) {
                roleList.add(authority.getAuthority());
            }

            //生成token
            // 生成token start
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            // 设置签发时间
            calendar.setTime(new Date());
            // 设置过期时间
            calendar.add(Calendar.MINUTE, 5);// 5分钟
            Date time = calendar.getTime();
            token = Jwts.builder()
                    .setSubject(auth.getName() + "-" + roleList)
                    //签发时间
                    .setIssuedAt(now)
                    //过期时间
                    .setExpiration(time)
                    .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY) //采用什么算法是可以自己选择的，不一定非要采用HS512
                    .compact();
            response.addHeader(ConstantKey.HEADER_KEY, ConstantKey.BEARER + token);

            // 登录成功后，返回token到body里面
            Map map = new HashMap();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(ConstantKey.HEADER_KEY, ConstantKey.BEARER + token);
            map.put("token", resultMap);
            map.put("message", "请求成功");
            response.setCharacterEncoding("UTF-8"); // 设置字符编码为UTF-8
            response.getWriter().write(JSON.toJSONString(map));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

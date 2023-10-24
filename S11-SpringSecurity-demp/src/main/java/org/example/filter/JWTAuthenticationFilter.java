package org.example.filter;

import io.jsonwebtoken.*;
import org.example.config.GrantedAuthorityImpl;
import org.example.constants.AuthWhiteList;
import org.example.constants.ConstantKey;
import org.example.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义JWT认证过滤器
 * 该类继承自BasicAuthenticationFilter，在doFilterInternal方法中，
 * 从http头的Authorization 项读取token数据，然后用Jwts包提供的方法校验token的合法性。
 * 如果校验通过，就认为这是一个取得授权的合法请求
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String header = request.getHeader(ConstantKey.HEADER_KEY);
        if (ObjectUtils.isEmpty(header) || !header.startsWith(ConstantKey.BEARER)) {
            chain.doFilter(request, response);
            return;
        }


        // 如果token不为空，并且是以指定票据开头
        if (!ObjectUtils.isEmpty(header) && header.startsWith(ConstantKey.BEARER)) {
            // 如果请求路径是放行路径，则直接跳过认证
            if (AuthWhiteList.AUTH_WHITELIST.contains(requestURI)) {
                chain.doFilter(request, response);
                return;
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request, response);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            long start = System.currentTimeMillis();
            String token = request.getHeader(ConstantKey.HEADER_KEY);
            if (ObjectUtils.isEmpty(token)) {
                throw new ServiceException("Token不能为空!");
            }

            String user = null;

            Claims claims = Jwts.parser().setSigningKey(ConstantKey.SIGNING_KEY).parseClaimsJws(token.replace(ConstantKey.BEARER, "")).getBody();
            // token签发时间
            long issuedAt = claims.getIssuedAt().getTime();
            // 当前时间
            long currentTimeMillis = System.currentTimeMillis();
            // token过期时间
            long expirationTime = claims.getExpiration().getTime();

            // 1. 签发时间 < 当前时间 < (签发时间+((token过期时间-token签发时间)/2)) 不刷新token
            // 2. (签发时间+((token过期时间-token签发时间)/2)) < 当前时间 < token过期时间 刷新token并返回给前端
            // 3. tokne过期时间 < 当前时间 跳转登录，重新登录获取token
            // 验证token时间有效性
            if ((issuedAt + ((expirationTime - issuedAt) / 2)) < currentTimeMillis && currentTimeMillis < expirationTime) {

                // 重新生成token start
                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                // 设置签发时间
                calendar.setTime(new Date());
                // 设置过期时间
                calendar.add(Calendar.MINUTE, 5);// 5分钟
                Date time = calendar.getTime();
                String refreshToken = Jwts.builder()
                        .setSubject(claims.getSubject())
                        .setIssuedAt(now)//签发时间
                        .setExpiration(time)//过期时间
                        .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY) //采用什么算法是可以自己选择的，不一定非要采用HS512
                        .compact();
                // 重新生成token end

                // 主动刷新token，并返回给前端
                response.addHeader("refreshToken", refreshToken);
            }
            long end = System.currentTimeMillis();
            logger.info("执行时间: {}", (end - start) + " 毫秒");
            user = claims.getSubject();
            if (user != null) {
                String[] authoritys = user.split("-")[1].split(",");
                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                for (int i = 0; i < authoritys.length; i++) {
                    String authority = authoritys[i];
                    // 处理解析权限异常，在注入权限的时候直接用的：用户名+数组，导致字符串中是"admin-[admin,xx1,xx2]"，而在解析的时候没有把两个括号[]去掉，导致权限识别错误，识别成了"[admin"和"xx2]"。
                    if (i == 0) {
                        authority = authority.replaceAll("\\[", "");
                    }
                    if (i == authoritys.length - 1) {
                        authority = authority.replaceAll("\\]", "");
                    }
                    authorities.add(new GrantedAuthorityImpl(authority));
                }
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }

        } catch (ExpiredJwtException e) {
            // 异常捕获、发送到ExpiredJwtException
            request.setAttribute("expiredJwtException", e);
            // 将异常分发到ExpiredJwtException控制器
            request.getRequestDispatcher("/expiredJwtException").forward(request, response);
        } catch (UnsupportedJwtException e) {
            // 异常捕获、发送到UnsupportedJwtException
            request.setAttribute("unsupportedJwtException", e);
            // 将异常分发到UnsupportedJwtException控制器
            request.getRequestDispatcher("/unsupportedJwtException").forward(request, response);
        } catch (MalformedJwtException e) {
            // 异常捕获、发送到MalformedJwtException
            request.setAttribute("malformedJwtException", e);
            // 将异常分发到MalformedJwtException控制器
            request.getRequestDispatcher("/malformedJwtException").forward(request, response);
        } catch (SignatureException e) {
            // 异常捕获、发送到SignatureException
            request.setAttribute("signatureException", e);
            // 将异常分发到SignatureException控制器
            request.getRequestDispatcher("/signatureException").forward(request, response);
        } catch (IllegalArgumentException e) {
            // 异常捕获、发送到IllegalArgumentException
            request.setAttribute("illegalArgumentException", e);
            // 将异常分发到IllegalArgumentException控制器
            request.getRequestDispatcher("/illegalArgumentException").forward(request, response);
        }
        return null;
    }


}

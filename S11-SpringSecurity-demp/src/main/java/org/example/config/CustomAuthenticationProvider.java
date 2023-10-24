package org.example.config;

import org.example.exception.ServiceException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义身份认证验证组件
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    /**
     *执行与以下合同相同的身份验证
     * {@link org.springframework.security.authentication.AuthenticationManager＃authenticate（Authentication）}
     *。
     *
     * @param authentication 身份验证请求对象。
     *
     * @返回包含凭证的经过完全认证的对象。 可能会回来
     * <code> null </ code>（如果<code> AuthenticationProvider </ code>无法支持）
     * 对传递的<code> Authentication </ code>对象的身份验证。 在这种情况下，
     * 支持所提供的下一个<code> AuthenticationProvider </ code>
     * 将尝试<code> Authentication </ code>类。
     *
     * @throws AuthenticationException 如果身份验证失败。
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        //通过用户名查找用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        if(userDetails == null){
            throw new UsernameNotFoundException("用户不存在!");
        }
        if(!bCryptPasswordEncoder.matches(password,userDetails.getPassword())){
            throw new ServiceException("密码错误!");
        }

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        authorityList.add(new GrantedAuthorityImpl("AUTH_WRITE"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(name, password, authorityList);
        return auth;
    }
    /**
     * 是否可以提供输入类型的认证服务
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

package org.example.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.controller.UserController;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl extends ServiceImpl<UserMapper, User> implements UserDetailsService ,UserService{

    protected Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public boolean sign(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不能为空!");
        }
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空!");
        }

        //账户名是否已存在
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        long count = super.count(wrapper.eq(User::getUsername, username));
        if(count > 0){
            throw new ServiceException("账户已存在");
        }

        //密码进行加密
        String encodePwd = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);

        logger.info("用户账号:{},加密后密码:{}",user.getUsername(),user.getPassword());
        boolean save = super.save(user);
        return save;
    }

//    @Override
//    public User login(User user) {
//
//        //数据库中查询
//        //账户名是否已存在
//        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
//
//        User userAccountByTable = getOne(wrapper.eq(User::getUsername,user.getUsername()));
//
//        //账号是否存在
//        if(Objects.isNull(userAccountByTable)){
//            throw new NullPointerException("账号不存在");
//        }
//
//        //比对密码是否正确
//        bCryptPasswordEncoder.matches()
//        if(!Objects.equals(userAccountByTable.getPassword(), MD5Utils.md5Hex(password))){
//            throw new ErrorPasswordException();
//        }
//
//        //账号是否冻结
//        UserStatusEnum userStatusEnum = UserStatusEnum.INSTANCE_MAP.get(userAccountByTable.getStatus());
//        if(userStatusEnum.equals(UserStatusEnum.FREEZE)){
//            throw new FreezeUserException();
//        }
//
//        //生成token存入redis中
//        String token = JwtUtil.getToken(BeanUtil.beanToMap(userAccount));
//        redisService.set(SecurityConstant.TOKEN_REDIS_KEY_PREXX + userAccountByTable.getId(),token);
//
//        //返回值对象
//        LoginSuccessVo loginSuccessVo = new LoginSuccessVo();
//        loginSuccessVo.setUserId(userAccountByTable.getId());
//        loginSuccessVo.setToken(token);
//
//        return loginSuccessVo;
//    }

    /**
     * 获取用户所拥有的权限列表
     *
     * @return
     */
    @GetMapping("/getAuthentication")
    public List<String> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            logger.info("权限列表：{}", grantedAuthority.getAuthority());
            list.add(grantedAuthority.getAuthority());
        }
        return list;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getOne(Wrappers.lambdaQuery(User.class).eq(User::getUsername, username));
    }

}

package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class UserController {


    @Autowired
    private UserService userDetailsService;

    @GetMapping("hello")
    public String hello() {
        return "hello security";
    }

//    @GetMapping("login")
//    @ResponseBody
//    public User login(@RequestBody User user) {
//        return userDetailsService.login(user);
//    }

    @PostMapping("sign")
    @ResponseBody
    public boolean sign(@RequestBody User user) {
        return userDetailsService.sign(user);

    }

    /**
     * 获取用户所拥有的权限列表
     *
     * @return
     */
    @GetMapping("/getAuthentication")
    public List<String> getAuthentication() {
        return userDetailsService.getAuthentication();
    }


}
package org.springframework.test.bean;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserService implements IUserService{

    private String token;
    @Override
    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "河南人都是神!";
    }

    @Override
    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }

    public String getToken() {
        return token;
    }

    public UserService setToken(String token) {
        this.token = token;
        return this;
    }
}

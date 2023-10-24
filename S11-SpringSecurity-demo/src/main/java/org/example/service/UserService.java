package org.example.service;

import org.example.entity.User;

import java.util.List;

public interface UserService {

    boolean sign(User user);

//    User login(User user);


    List<String> getAuthentication();

}

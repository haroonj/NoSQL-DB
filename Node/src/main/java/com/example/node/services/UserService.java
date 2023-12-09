package com.example.node.services;


import com.example.node.model.system.User;
import com.example.node.util.system.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserUtil userUtil;

    public UserService(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    public User getUserByUsername(String username){
        return userUtil.findByUsername(username);
    }

    public User getUserById(String id){
        return userUtil.findById(id);
    }

}

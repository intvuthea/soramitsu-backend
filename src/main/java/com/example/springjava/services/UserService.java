package com.example.springjava.services;

import com.example.springjava.models.User;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUser(String username);
    List<User> getUsers();
}

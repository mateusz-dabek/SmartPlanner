package com.smartplanner.service;

import com.smartplanner.model.dto.UserDto;
import com.smartplanner.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    boolean findUserById(int id);

    User getUserById(int id);

    User saveUser(UserDto user);

    List<User> findAll();

    void delete(int id);

    User findByUsername(String username);

    User findById(int id);
}

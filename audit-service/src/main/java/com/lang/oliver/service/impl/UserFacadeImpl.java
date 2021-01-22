package com.lang.oliver.service.impl;

import com.lang.oliver.service.domain.User;
import com.lang.oliver.service.facade.UserFacade;
import com.lang.oliver.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserFacadeImpl implements UserFacade {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    @Override
    public Integer addUser(@RequestBody User user) {
         userRepository.insert(user);
         return user.getId();
    }

}

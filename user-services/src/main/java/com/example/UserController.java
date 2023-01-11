package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
    }

    @GetMapping("/user")
    public User getUserByName(@RequestParam("userName") String userName) throws Exception{
        return  userService.getUserByUserName(userName);
    }

    @GetMapping("/user/{age}")
    public List<User> getAllUsersByAge(@PathVariable int age) throws Exception{
        return userService.getAllUsersByAge(age);
    }
}

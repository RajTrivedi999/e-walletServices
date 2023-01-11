package com.example;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(){
        super("User not found");
    }
}

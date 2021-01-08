package com.toy.getyourfriday.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer id) {
        super("User id: " + id + " has not been found");
    }
}

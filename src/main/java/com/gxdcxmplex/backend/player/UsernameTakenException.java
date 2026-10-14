package com.gxdcxmplex.backend.player;

public class UsernameTakenException extends RuntimeException{
    public UsernameTakenException() {
        super("Username already taken");
    }
}

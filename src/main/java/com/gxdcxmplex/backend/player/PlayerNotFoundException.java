package com.gxdcxmplex.backend.player;

public class PlayerNotFoundException extends RuntimeException{

    public PlayerNotFoundException(String message){
        super(message);
    }
}

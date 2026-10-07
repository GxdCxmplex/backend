package com.gxdcxmplex.backend.player;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreatePlayerRequest {

    @NotBlank
    private String username;

    @Min(1)
    private int level;

    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public int getLevel(){
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

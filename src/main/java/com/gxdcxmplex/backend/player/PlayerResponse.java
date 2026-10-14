package com.gxdcxmplex.backend.player;

public class PlayerResponse {

    private Long id;
    private String username;
    private int level;

    public PlayerResponse(Long id, String username, int level){
        this.id = id;
        this.username = username;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public static PlayerResponse from(Player player) {
        return new PlayerResponse(player.getId(), player.getUsername(), player.getLevel());
    }
}

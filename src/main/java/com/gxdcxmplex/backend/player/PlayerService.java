package com.gxdcxmplex.backend.player;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }
    public List<Player> getPlayers(){
        return playerRepository.findAll();
    }
    public Player getPlayer(Long id){
        return playerRepository.findById(id).orElseThrow();
    }
    public Player createPlayer(Player player){
        return playerRepository.save(player);
    }

    public void deletePlayer(Long id){
        playerRepository.deleteById(id);
    }

    public Player putPlayer(Long id, String username, int level){
        Player player = playerRepository.findById(id).orElseThrow(()-> new RuntimeException("Player not found"));
        player.setLevel(level);
        player.setUsername(username);

        return playerRepository.save(player);
    }
}

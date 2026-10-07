package com.gxdcxmplex.backend.player;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder){
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Player> getPlayers(){
        return playerRepository.findAll();
    }
    public Player getPlayer(Long id){
        return playerRepository.findById(id).orElseThrow(()-> new PlayerNotFoundException("Player not found"));
    }
    public Player createPlayer(Player player){
        return playerRepository.save(player);
    }

    public void deletePlayer(Long id){
        playerRepository.deleteById(id);
    }

    public Player putPlayer(Long id, String username, int level){
        Player player = playerRepository.findById(id).orElseThrow(()-> new PlayerNotFoundException("Player not found"));
        player.setLevel(level);
        player.setUsername(username);

        return playerRepository.save(player);
    }

    public PlayerResponse register(RegisterPlayerRequest request){
        Player player = new Player(request.getUsername(), 1);
        player.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Player savedPlayer = playerRepository.save(player);
        return new PlayerResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getLevel());
    }
}

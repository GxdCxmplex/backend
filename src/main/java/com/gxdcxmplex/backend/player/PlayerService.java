package com.gxdcxmplex.backend.player;

import com.gxdcxmplex.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public List<PlayerResponse> getPlayers(){
        return playerRepository.findAll()
                .stream()
                .map(player -> new PlayerResponse(player.getId(), player.getUsername(), player.getLevel()))
                .toList();
    }
    public PlayerResponse getPlayer(Long id){
        Player player = playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException("Player not found"));
        return new PlayerResponse(player.getId(), player.getUsername(), player.getLevel());
    }
    public PlayerResponse createPlayer(Player player){
        Player savedPlayer = playerRepository.save(player);
        return new PlayerResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getLevel());
    }

    public void deletePlayer(Long id){
        playerRepository.deleteById(id);
    }

    public PlayerResponse putPlayer(Long id, String username, int level){
        Player player = playerRepository.findById(id).orElseThrow(()-> new PlayerNotFoundException("Player not found"));
        player.setLevel(level);
        player.setUsername(username);

        Player savedPlayer = playerRepository.save(player);

        return new PlayerResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getLevel());
    }

    public PlayerResponse register(RegisterPlayerRequest request){
        if (playerRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        Player player = new Player(request.getUsername(), 1);
        player.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Player savedPlayer = playerRepository.save(player);
        return new PlayerResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getLevel());
    }

    public String login(LoginRequest request){
        Player player = playerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new PlayerNotFoundException("invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPasswordHash())){
            throw new PlayerNotFoundException("Invalid username or password");
        }

        return jwtService.generateToken(player.getUsername());
    }

    public PlayerResponse getPlayerByUsername(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        return new PlayerResponse(player.getId(), player.getUsername(), player.getLevel());
    }
}

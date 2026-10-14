package com.gxdcxmplex.backend.player;

import com.gxdcxmplex.backend.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
                .map(PlayerResponse::from)
                .toList();
    }

    public PlayerResponse register(RegisterPlayerRequest request){
        if (playerRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameTakenException();
        }
        Player player = new Player(request.getUsername(), 1);
        player.setPasswordHash(passwordEncoder.encode(request.getPassword()));
       try {
           Player savedPlayer = playerRepository.saveAndFlush(player);
           return PlayerResponse.from(savedPlayer);
       } catch (DataIntegrityViolationException e){
           throw new UsernameTakenException();
       }
    }

    public String login(LoginRequest request){
        Player player = playerRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), player.getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(player.getUsername());
    }

    public PlayerResponse getPlayerByUsername(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        return new PlayerResponse(player.getId(), player.getUsername(), player.getLevel());
    }
    @Transactional
    public void deletePlayerByUsername(String username) {
        playerRepository.findByUsername(username).ifPresent(playerRepository::delete);
    }
}

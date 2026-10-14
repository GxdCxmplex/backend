package com.gxdcxmplex.backend.player;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public record LoginResponse(String token) {}

    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }
    @GetMapping
    public List<PlayerResponse> getPlayers(){
        return playerService.getPlayers();
    }

    @PostMapping("/register")
    public ResponseEntity<PlayerResponse> register(@Valid @RequestBody RegisterPlayerRequest request){
        PlayerResponse response = playerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return new LoginResponse(playerService.login(request));
    }

    @GetMapping("/me")
    public PlayerResponse me(Authentication authentication) {
        return playerService.getPlayerByUsername(authentication.getName());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Authentication authentication){
        playerService.deletePlayerByUsername(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

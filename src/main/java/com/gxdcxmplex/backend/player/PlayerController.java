package com.gxdcxmplex.backend.player;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }
    @GetMapping
    public List<PlayerResponse> getPlayers(){
        return playerService.getPlayers();
    }
    @GetMapping("/{id}")
    public PlayerResponse getPlayer(@PathVariable Long id){
        return playerService.getPlayer(id);
    }

    @PostMapping
    public PlayerResponse createPlayer(@Valid @RequestBody CreatePlayerRequest request){
        Player player = new Player(request.getUsername(), request.getLevel());
        return playerService.createPlayer(player);
    }

    @PostMapping("/register")
    public PlayerResponse register(@Valid @RequestBody RegisterPlayerRequest request){
        return playerService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        String token = playerService.login(request);

        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id){
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public PlayerResponse putPlayer(@PathVariable Long id, @RequestBody CreatePlayerRequest request){
        return playerService.putPlayer(id, request.getUsername(), request.getLevel());
    }
}

package com.gxdcxmplex.backend.player;

import com.gxdcxmplex.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    private PlayerRepository playerRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerRepository = mock(PlayerRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = new JwtService();

        playerService = new PlayerService(playerRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerSavesHashedPasswordAndReturnSafeResponse() {
        RegisterPlayerRequest request = new RegisterPlayerRequest();
        request.setUsername("TestPlayer");
        request.setPassword("StrongPassword123");

        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            try {
                var idField = Player.class.getDeclaredField("Id");
                idField.setAccessible(true);
                idField.set(player, 123L);
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException(exception);
            }
            return player;
        });

        PlayerResponse response = playerService.register(request);

        assertEquals(123L, response.getId());
        assertEquals("TestPlayer", response.getUsername());
        assertEquals(1, response.getLevel());

        var captor = org.mockito.ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());

        Player savedPlayer = captor.getValue();
        assertNotEquals("StrongPassword123", savedPlayer.getPasswordHash());
        assertTrue(passwordEncoder.matches("StrongPassword123", savedPlayer.getPasswordHash()));
    }

    @Test
    void registerRejectDuplicateUsername() {
        RegisterPlayerRequest request = new RegisterPlayerRequest();
        request.setUsername("ExistingPlayer");
        request.setPassword("StrongPassword123");

        when(playerRepository.findByUsername("ExistingPlayer"))
                .thenReturn(Optional.of(new Player("ExistingPlayer", 1)));

        assertThrows(IllegalArgumentException.class, () -> playerService.register(request));
        verify(playerRepository, never()).save(any(Player.class));

    }
}

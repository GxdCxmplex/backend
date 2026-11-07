package com.gxdcxmplex.backend.player;

import com.gxdcxmplex.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.parameters.P;
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
        jwtService = mock(JwtService.class);
        playerService = new PlayerService(playerRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerSavesHashedPasswordAndReturnSafeResponse() {
        RegisterPlayerRequest request = new RegisterPlayerRequest();
        request.setUsername("TestPlayer");
        request.setPassword("StrongPassword123");

        when(playerRepository.saveAndFlush(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            try {
                var idField = Player.class.getDeclaredField("id");
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
        verify(playerRepository).saveAndFlush(captor.capture());

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

        assertThrows(UsernameTakenException.class, () -> playerService.register(request));
        verify(playerRepository, never()).saveAndFlush(any(Player.class));

    }

    @Test
    void registerHandlesRaceConditionOnUniqueConstraint() {
        RegisterPlayerRequest request = new RegisterPlayerRequest();
        request.setUsername("RacePlayer");
        request.setPassword("StrongPassword123");
        when(playerRepository.findByUsername("RacePlayer")).thenReturn(Optional.empty());
        when(playerRepository.saveAndFlush(any(Player.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(UsernameTakenException.class, () -> playerService.register(request));
    }

    @Test
    void loginReturnsTokenForCorrectPassword() {
        Player player = new Player("TestPlayer", 1);
        player.setPasswordHash(passwordEncoder.encode("StrongPassword123"));
        when(playerRepository.findByUsername("TestPlayer")).thenReturn(Optional.of(player));
        when(jwtService.generateToken("TestPlayer")).thenReturn("token123");

        LoginRequest request = new LoginRequest();
        request.setUsername("TestPlayer");
        request.setPassword("StrongPassword123");

        assertEquals("token123", playerService.login(request));
    }

    @Test
    void loginRejectsWrongPassword() {
        Player player = new Player("TestPlayer", 1);
        player.setPasswordHash(passwordEncoder.encode("StrongPassword123"));
        when(playerRepository.findByUsername("TestPlayer")).thenReturn(Optional.of(player));

        LoginRequest request = new LoginRequest();
        request.setUsername("TestPlayer");
        request.setPassword("WrongPassword");

        assertThrows(InvalidCredentialsException.class, () -> playerService.login(request));
    }
}

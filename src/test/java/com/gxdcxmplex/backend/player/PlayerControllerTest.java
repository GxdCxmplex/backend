package com.gxdcxmplex.backend.player;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gxdcxmplex.backend.security.JwtService;
import com.gxdcxmplex.backend.security.JwtAuthenticationFilter;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getPlayerReturnsPlayerList() throws Exception {
        when(playerService.getPlayers()).thenReturn(
                List.of(new PlayerResponse(1l, "TestPlayer", 5))
        );

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("TestPlayer"))
                .andExpect(jsonPath("$[0].level").value(5));
    }

    @Test
    void registerRejectsBlankUsername() throws Exception{
        String invalidRequest = """
                {
                    "username": " ",
                    "password": "StrongPassword123"
                }
                """;

        mockMvc.perform(post("/players/register")
                .contentType("application/json")
                .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(playerService);
    }

    @Test
    void registerReturnsCreatedPlayer() throws Exception {
        PlayerResponse response = new PlayerResponse(1L, "TestPlayer", 1);

        when(playerService.register(any(RegisterPlayerRequest.class)))
                .thenReturn(response);

        String request = """
                {
                    "username": "TestPlayer",
                    "password": "StrongPassword
                }
                """;

        mockMvc.perform(post("/players/register")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestPlayer"))
                .andExpect(jsonPath("$.level").value(1));
    }
}

package com.matchmaking.elo.licenta.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.matchmaking.elo.licenta.model.Match;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.MatchRepository;
import com.matchmaking.elo.licenta.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateUser_ExistingUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEloRating(1000);
        existingUser.setName("John Doe");

        User updatedUser = new User();
        updatedUser.setEloRating(1100);
        updatedUser.setName("Jane Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(userId, updatedUser);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals(1100, result.getEloRating());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testDeleteUser_UserExists() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).deleteById(userId);

        boolean result = userService.deleteUser(userId);

        assertTrue(result);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testDeleteUser_UserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(userId);

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testSignIn_UserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setSignedIn(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        boolean result = userService.signIn(userId);

        assertTrue(result);
        assertTrue(user.isSignedIn());
        verify(userRepository).save(user);
    }

    @Test
    public void testSignIn_UserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.signIn(userId);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdatePlayerEloRating_PlayerWins() {
        User player = new User();
        player.setId(1L);
        player.setEloRating(1000);

        User opponent = new User();
        opponent.setId(2L);
        opponent.setEloRating(1200);

        boolean playerWon = true;

//        userService.updatePlayerEloRating(player, opponent, playerWon);

        double playerExpectation = 1 / (1 + Math.pow(10, (1200 - 1000) / 400.0));
        int expectedElo = (int) (1000 + userService.K_FACTOR_CONSTANT * (1 - playerExpectation));
        expectedElo = Math.max(expectedElo, 0); // Asiguram că ELO nu scade sub zero

        assertEquals(expectedElo, player.getEloRating());
        verify(userRepository).save(player);
    }

    @Test
    public void testUpdatePlayerEloRating_PlayerLoses() {
        User player = new User();
        player.setId(1L);
        player.setEloRating(1000);

        User opponent = new User();
        opponent.setId(2L);
        opponent.setEloRating(1200);

        boolean playerWon = false;

//        userService.updatePlayerEloRating(player, opponent, playerWon);

        double playerExpectation = 1 / (1 + Math.pow(10, (1200 - 1000) / 400.0));
        int expectedElo = (int) (1000 + userService.K_FACTOR_CONSTANT * (0 - playerExpectation));
        expectedElo = Math.max(expectedElo, 0);

        assertEquals(expectedElo, player.getEloRating());
        verify(userRepository).save(player);
    }


    // TODO: Alte teste pentru metode diferite
}

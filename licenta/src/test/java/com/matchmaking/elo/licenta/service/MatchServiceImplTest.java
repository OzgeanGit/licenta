package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Match;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.MatchRepository;
import com.matchmaking.elo.licenta.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testSaveMatch() {
        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);
        match.setPlayer1Id(1L);
        match.setPlayer2Id(2L);

        User player1 = new User(1L);
        User player2 = new User(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(player1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(player2));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        Match result = matchService.saveMatch(match);

        assertNotNull(result);
        assertEquals(matchId, result.getId());
        verify(userRepository, times(2)).save(any(User.class));
        verify(matchRepository).save(match);
    }

    @Test
    public void testGetMatchById_MatchFound() {
        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        Optional<Match> result = matchService.getMatchById(matchId);

        assertNotNull(result);
        assertEquals(matchId, result.get().getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetMatchById_MatchNotFound() {
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        matchService.getMatchById(matchId);
    }

    @Test
    public void testDeleteMatch_MatchExists() {
        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        doNothing().when(matchRepository).deleteById(matchId);

        boolean result = matchService.deleteMatch(matchId);

        assertTrue(result);
        verify(matchRepository).deleteById(matchId);
    }

    @Test
    public void testDeleteMatch_MatchDoesNotExist() {
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        boolean result = matchService.deleteMatch(matchId);

        assertFalse(result);
        verify(matchRepository, never()).deleteById(matchId);
    }

    @Test
    public void testUpdateMatch() {
        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);

        when(matchRepository.save(match)).thenReturn(match);

        Match result = matchService.updateMatch(match);

        assertNotNull(result);
        assertEquals(matchId, result.getId());
        verify(matchRepository).save(match);
    }




}

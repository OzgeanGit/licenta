package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.League;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.DivisionRepository;
import com.matchmaking.elo.licenta.repository.LeagueRepository;
import com.matchmaking.elo.licenta.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LeagueServiceImplTest {

    @Mock
    private LeagueRepository leagueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DivisionRepository divisionRepository;

    @InjectMocks
    private LeagueServiceImpl leagueService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLeagueById_LeagueFound() {
        Long leagueId = 1L;
        League league = new League();
        league.setId(leagueId);
        when(leagueRepository.findById(leagueId)).thenReturn(Optional.of(league));
        League result = leagueService.getLeagueById(leagueId);
        assertNotNull(result);
        assertEquals(leagueId, result.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetLeagueById_LeagueNotFound() {
        Long leagueId = 1L;
        when(leagueRepository.findById(leagueId)).thenReturn(Optional.empty());
        leagueService.getLeagueById(leagueId);
    }

    @Test
    public void testDeleteLeague() {
        Long leagueId = 1L;
        doNothing().when(leagueRepository).deleteById(leagueId);
        leagueService.deleteLeague(leagueId);
        verify(leagueRepository).deleteById(leagueId);
    }

    @Test
    public void testCreateLeague() {
        String leagueName = "Premier League";
        League league = new League();
        league.setName(leagueName);
        when(leagueRepository.save(any(League.class))).thenReturn(league);
        League result = leagueService.createLeague(leagueName);
        assertNotNull(result);
        assertEquals(leagueName, result.getName());
    }

    @Test
    public void testAddPlayerToLeague() {
        Long leagueId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setLeagueId(leagueId);
            return null;
        }).when(userRepository).save(any(User.class));

        leagueService.addPlayerToLeague(leagueId, userId);

        assertEquals(leagueId, user.getLeagueId());
        verify(userRepository).save(user);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddPlayerToLeague_UserNotFound() {
        Long leagueId = 1L;
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        leagueService.addPlayerToLeague(leagueId, userId);
    }

    @Test
    public void testRemovePlayerFromLeague() {
        Long leagueId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setLeagueId(leagueId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setLeagueId(null);
            return null;
        }).when(userRepository).save(any(User.class));

        leagueService.removePlayerFromLeague(leagueId, userId);

        assertNull(user.getLeagueId());
        verify(userRepository).save(user);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemovePlayerFromLeague_UserNotInLeague() {
        Long leagueId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setLeagueId(2L); // Diferit de leagueId

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        leagueService.removePlayerFromLeague(leagueId, userId);
    }

    @Test
    public void testDistributePlayersInDivisions() {
        Long leagueId = 1L;

        List<User> users = Arrays.asList(
                User.createUser(1L, "User1", 1200, LocalDate.now(), null, leagueId, true),
                User.createUser(2L, "User2", 1100, LocalDate.now(), null, leagueId, true),
                User.createUser(3L, "User3", 1211, LocalDate.now(), null, leagueId, true),
                User.createUser(4L, "User4", 1140, LocalDate.now(), null, leagueId, true)

        );

        List<Division> divisions = Arrays.asList(
                new Division(),
                new Division()
        );

        divisions.forEach(d -> d.setLeagueId(leagueId));
        divisions.get(0).setRank(1);
        divisions.get(0).setId(1);
        divisions.get(1).setRank(2);
        divisions.get(1).setId(2);


        when(userRepository.findByLeagueId(leagueId)).thenReturn(users);
        when(divisionRepository.findByLeagueId(leagueId)).thenReturn(divisions);

        boolean result = leagueService.distributePlayersInDivisions(leagueId);

        assertTrue(result);
        for (User user : users) {
            assertNotNull(user.getDivisionId());
            verify(userRepository).save(user);
        }
    }

    @Test
    public void testDistributePlayersInDivisions_NoDivisions() {
        Long leagueId = 1L;

        List<User> users = Arrays.asList(
                User.createUser(1L, "User1", 1200, LocalDate.now(), null, leagueId, true),
                User.createUser(2L, "User2", 1100, LocalDate.now(), null, leagueId, true)
        );

        when(userRepository.findByLeagueId(leagueId)).thenReturn(users);
        when(divisionRepository.findByLeagueId(leagueId)).thenReturn(Collections.emptyList());

        boolean result = leagueService.distributePlayersInDivisions(leagueId);

        assertFalse(result);
        for (User user : users) {
            verify(userRepository, never()).save(user);
        }
    }




}

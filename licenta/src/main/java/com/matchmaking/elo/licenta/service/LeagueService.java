package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.League;
import com.matchmaking.elo.licenta.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing leagues.
 */
public interface LeagueService {
    League getLeagueById(Long leagueId);

    League createLeague(String name);

    Optional<League> getLeague(Long id);

    List<League> getAllLeagues();

    void deleteLeague(Long id);

    List<Division> getAllDivisionsInLeague(Long leagueId);

    void endSeason(Long leagueId);

    void addPlayerToLeague(Long leagueId, Long userId);

    void removePlayerFromLeague(Long leagueId, Long userId);

    boolean distributePlayersInDivisions(Long leagueId);

    void addDivisionToLeague(Long leagueId, Division division);

    void removeDivisionFromLeague(Long leagueId, Long divisionId);

    League save(League league);


}

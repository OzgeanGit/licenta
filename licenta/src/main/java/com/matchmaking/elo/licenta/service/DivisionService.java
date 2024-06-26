package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.Pair;
import com.matchmaking.elo.licenta.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing divisions.
 */
public interface DivisionService {
    Division createDivision(String name);

    Optional<Division> getDivision(Long id);

    List<Division> getAllDivisions();

    void delete(Division division);

    List<User> getDivisionStandings(long divisionId);

    List<Pair> matchPlayersInDivision(long divisionId);

    List<User> getSignedInUsersInDivision(long divisionId);

    List<Pair> matchPlayersInDivisionWeighted(long divisionId);

    List<Pair> matchPlayersOptimally(Long divisionId);

    Division save(Division division);

    List<Division> getAllDivisionsInLeague(Long leagueId);


}

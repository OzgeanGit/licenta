package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Match;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing matches.
 */
public interface MatchService {
    Match saveMatch(Match match);
    Optional<Match> getMatchById(Long id);
    List<Match> getAllMatches();
    boolean deleteMatch(Long id);
    Match updateMatch(Match match);

    void updatePlayersEloRating(Match match);

    double calculateExpectedScore(int playerElo, int opponentElo);

    List<Match> getMatchesByUserId(Long userId);

    Long getNumberOfMatchesBetweenPlayers(Long player1Id, Long player2Id);

    Long getNumberOfPlayerWins(Long userId);
    Long getNumberOfPlayerLosses(Long userId);

    int calculateRecentPerformance(Long userId);
}

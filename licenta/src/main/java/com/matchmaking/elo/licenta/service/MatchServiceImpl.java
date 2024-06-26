package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Match;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing matches.
 * This service provides functionality for creating, retrieving, updating, and deleting matches,
 * as well as handling ELO rating calculations and match history retrieval.
 */
@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchServiceImpl.class);
    static final int K_FACTOR_CONSTANT = 32;
    private final MatchRepository matchRepository;
    private final UserService userService;


    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, UserService userService) {
        this.matchRepository = matchRepository;
        this.userService = userService;
    }


    /**
     * Saves a new match and updates the match counter for the involved players.
     *
     * @param match the match to save
     * @return the saved match
     * @throws IllegalArgumentException if either player is not found
     */
    @Override
    public Match saveMatch(Match match) {
        User player1 = userService.findUserById(match.getPlayer1Id())
                .orElseThrow(() -> new IllegalArgumentException("Player 1 not found"));
        User player2 = userService.findUserById(match.getPlayer2Id())
                .orElseThrow(() -> new IllegalArgumentException("Player 2 not found"));

        player1.setMatchCounter(player1.getMatchCounter() + 1);
        player2.setMatchCounter(player2.getMatchCounter() + 1);

        userService.save(player1);
        userService.save(player2);

        logger.info("Saving match: {}", match);

        return matchRepository.save(match);
    }

    /**
     * Retrieves a match by its ID.
     *
     * @param id the ID of the match to retrieve
     * @return an Optional containing the match if found, or an empty Optional if not found
     */
    @Override
    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }


    /**
     * Retrieves all matches.
     *
     * @return a list of all matches
     */
    @Override
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    /**
     * Deletes a match by its ID.
     *
     * @param id the ID of the match to delete
     * @return true if the match was deleted, false if the match was not found
     */
    @Override
    public boolean deleteMatch(Long id) {
        Optional<Match> match = matchRepository.findById(id);
        if (match.isPresent()) {
            matchRepository.deleteById(id);
            logger.info("Deleted match with ID {}", id);
            return true;
        }
        return false;
    }

    /**
     * Updates an existing match.
     *
     * @param match the match to update
     * @return the updated match
     */
    @Override
    public Match updateMatch(Match match) {
        logger.info("Updated match: {}", match);
        return matchRepository.save(match);

    }

    /**
     * Calculates the new ELO rating for a player after a match.
     *
     * @param currentElo the current ELO rating of the player
     * @param opponentElo the ELO rating of the opponent
     * @param playerScore the score of the player
     * @param opponentScore the score of the opponent
     * @return the new ELO rating of the player
     */
    public int calculateNewElo(int currentElo, int opponentElo, int playerScore, int opponentScore) {
        double expectedScore = calculateExpectedScore(currentElo, opponentElo);
        double actualScore;
        if(playerScore > opponentScore){
            actualScore = 1;
        }
        else if(playerScore < opponentElo){
            actualScore = 0;
        }
        else actualScore = 0.5;
        int newElo = (int) (currentElo + K_FACTOR_CONSTANT * (actualScore - expectedScore));
        logger.info("Calculated new ELO: currentElo={}, opponentElo={}, playerScore={}, opponentScore={}, newElo={}",
                currentElo, opponentElo, playerScore, opponentScore, newElo);
        return Math.max(newElo, 0);
    }

    /**
     * Updates the ELO ratings of the players involved in a match.
     *
     * @param match the match for which to update the ELO ratings
     * @throws IllegalArgumentException if either player is not found
     */
    @Override
    public void updatePlayersEloRating(Match match) {
        User player1 = userService.findUserById(match.getPlayer1Id())
                .orElseThrow(() -> new IllegalArgumentException("Player 1 not found"));
        User player2 = userService.findUserById(match.getPlayer2Id())
                .orElseThrow(() -> new IllegalArgumentException("Player 2 not found"));

        match.setPlayer1EloAtMatchTime(player1.getEloRating());
        match.setPlayer2EloAtMatchTime(player2.getEloRating());

        int player1NewElo = calculateNewElo(player1.getEloRating(), player2.getEloRating(), match.getPlayer1Score(), match.getPlayer2Score());
        int player2NewElo = calculateNewElo(player2.getEloRating(), player1.getEloRating(), match.getPlayer2Score(), match.getPlayer1Score());

        userService.updatePlayerElo(player1, player1NewElo);
        userService.updatePlayerElo(player2, player2NewElo);

        match.setPlayer1EloAfterMatch(player1NewElo);
        match.setPlayer2EloAfterMatch(player2NewElo);

        logger.info("Updated players' ELO after match: matchId={}, player1NewElo={}, player2NewElo={}", match.getId(), player1NewElo, player2NewElo);

    }

    /**
     * Calculates the expected score for a player based on their ELO rating and the opponent's ELO rating.
     *
     * @param playerElo the ELO rating of the player
     * @param opponentElo the ELO rating of the opponent
     * @return the expected score for the player
     */
    @Override
    public double calculateExpectedScore(int playerElo, int opponentElo) {
        double expectedScore = 1 / (1 + Math.pow(10, (opponentElo - playerElo) / 400.0));
        logger.info("Calculated expected score: playerElo={}, opponentElo={}, expectedScore={}", playerElo, opponentElo, expectedScore);
        return expectedScore;
    }

    /**
     * Retrieves all matches involving a specific user.
     *
     * @param userId the ID of the user
     * @return a list of matches involving the user, sorted by match date
     */
    @Override
    public List<Match> getMatchesByUserId(Long userId) {
        List<Match> matches = matchRepository.findAll().stream()
                .filter(match -> match.getPlayer1Id().equals(userId) || match.getPlayer2Id().equals(userId))
                .sorted(Comparator.comparing(Match::getMatchDateTime))
                .collect(Collectors.toList());
        logger.info("Fetched matches for user {}: {}", userId, matches);
        return matches;
    }

    /**
     * Gets the number of matches between two players.
     *
     * @param player1Id the ID of the first player
     * @param player2Id the ID of the second player
     * @return the number of matches between the two players
     */
    @Override
    public Long getNumberOfMatchesBetweenPlayers(Long player1Id, Long player2Id) {
        Long matchCount = matchRepository.countByPlayer1IdAndPlayer2Id(player1Id, player2Id);
        logger.info("Number of matches between players {} and {}: {}", player1Id, player2Id, matchCount);
        return matchCount;
    }

    /**
     * Gets the number of wins for a player.
     *
     * @param userId the ID of the player
     * @return the number of wins for the player
     */
    @Override
    public Long getNumberOfPlayerWins(Long userId) {
        return matchRepository.countByWinnerId(userId);
    }

    /**
     * Gets the number of losses for a player.
     *
     * @param userId the ID of the player
     * @return the number of losses for the player
     */
    @Override
    public Long getNumberOfPlayerLosses(Long userId) {
        return matchRepository.countByLoserId(userId);
    }

    /**
     * Calculates the recent performance of a player based on their last 5 matches.
     *
     * @param userId the ID of the player
     * @return the recent performance score of the player
     */
    @Override
    public int calculateRecentPerformance(Long userId) {
        List<Match> allMatches = getAllMatches()
                .stream()
                .filter(match -> match.getPlayer1Id().equals(userId) || match.getPlayer2Id().equals(userId))
                .sorted(Comparator.comparing(Match::getId).reversed())
                .collect(Collectors.toList());

        List<Match> recentMatches = allMatches.subList(0, Math.min(5, allMatches.size()));
        int wins = 0;
        for (Match match : recentMatches) {
            if (match.getWinnerId().equals(userId)) {
                wins++;
            }
        }
        int performance = 25 * wins;
        logger.info("Calculated recent performance for user {}: {}", userId, performance);
        return performance;
    }

}

package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.Pair;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.DivisionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementation for managing divisions.
 * This service provides functionality for creating, retrieving, updating, and deleting divisions,
 * as well as handling player matchmaking and standings retrieval within divisions.
 */
@Service
public class DivisionServiceImpl implements DivisionService {

    private static final Logger logger = LoggerFactory.getLogger(DivisionServiceImpl.class);

    private final DivisionRepository divisionRepository;

    private final UserService userService;

    private final MatchService matchService;


    @Autowired
    public DivisionServiceImpl(DivisionRepository divisionRepository, UserService userService, MatchService matchService) {
        this.divisionRepository = divisionRepository;
        this.userService = userService;
        this.matchService = matchService;
    }

    /**
     * Creates a new division with the given name.
     *
     * @param name the name of the new division
     * @return the created division
     */
    @Override
    public Division createDivision(String name) {
        Division division = new Division();
        division.setName(name);
        Division createdDivision = divisionRepository.save(division);
        logger.info("Created new division: {}", createdDivision);
        return createdDivision;
    }

    /**
     * Retrieves a division by its ID.
     *
     * @param id the ID of the division to retrieve
     * @return an Optional containing the division if found, or an empty Optional if not found
     */
    @Override
    public Optional<Division> getDivision(Long id) {
        return divisionRepository.findById(id);
    }

    /**
     * Retrieves all divisions.
     *
     * @return a list of all divisions
     */
    @Override
    public List<Division> getAllDivisions() {
        return divisionRepository.findAll();
    }

    /**
     * Deletes a division.
     *
     * @param division the division to delete
     */
    @Override
    public void delete(Division division) {
        divisionRepository.delete(division);
        logger.info("Deleted division: {}", division);

    }

    /**
     * Retrieves the standings for a division, sorted by ELO rating in descending order.
     *
     * @param divisionId the ID of the division
     * @return a list of users in the division, sorted by ELO rating
     */
    @Override
    public List<User> getDivisionStandings(long divisionId) {
        List<User> standings = userService.findUsersInDivisionSortedByEloDescending(divisionId);
        logger.info("Fetched division standings for division {}: {}", divisionId, standings);
        return standings;

    }

    /**
     * Retrieves all signed-in users in a division.
     *
     * @param divisionId the ID of the division
     * @return a list of signed-in users in the division
     */
    @Override
    public List<User> getSignedInUsersInDivision(long divisionId) {
        return userService.getAllSignedInUsersInDivision(divisionId);
    }

    /**
     * Matches players in a division based on their ELO ratings.
     * This method is less efficient and is intended for use with the Hungarian algorithm(TO BE IMPLEMENTED).
     *
     * @param divisionId the ID of the division
     * @return a list of matched pairs of users
     * @throws IllegalStateException if there are not enough players signed in for matchmaking
     */
    @Override
    public List<Pair> matchPlayersInDivision(long divisionId) {
        List<User> signedInUsers = getSignedInUsersInDivision(divisionId);
        TreeSet<User> sortedUsers = new TreeSet<>(Comparator.comparing(User::getEloRating));
        sortedUsers.addAll(signedInUsers);

        if (signedInUsers.size() < 2) {
            throw new IllegalStateException("Not enough players signed in for matchmaking in division " + divisionId);
        }

        List<Pair> pairs = new ArrayList<>();

        while (sortedUsers.size() > 1) {
            User user1 = sortedUsers.pollFirst(); // Scoate primul user
            User user2 = findOptimalPlayerPair(user1, sortedUsers);
            if (user2 != null) {
                pairs.add(new Pair(user1.getId(), user2.getId()));
                sortedUsers.remove(user2);
                logger.info("Matched players: user1={}, user2={}", user1.getId(), user2.getId());

            }
        }

        logger.info("Completed matchmaking for division {}: {}", divisionId, pairs);
        return pairs;
    }

    private User findOptimalPlayerPair(User user, TreeSet<User> sortedUsers) {
        User lower = sortedUsers.lower(user);
        User higher = sortedUsers.higher(user);

        if (lower == null && higher == null) {
            return null;
        } else if (lower == null) {
            return higher;
        } else if (higher == null) {
            return lower;
        }

        double lowerDiff = Math.pow(user.getEloRating() - lower.getEloRating(), 2) + calculateCompatibilityScore(user.getId(), lower.getId());
        double higherDiff = Math.pow(user.getEloRating() - higher.getEloRating(), 2) + calculateCompatibilityScore(user.getId(), higher.getId());

        return lowerDiff < higherDiff ? lower : higher;
    }

    /**
     * Matches players in a division based on a weighted score.
     * This is a secondary method used for player matching.
     *
     * @param divisionId the ID of the division
     * @return a list of matched pairs of users
     * @throws IllegalStateException if there are not enough players signed in for matchmaking
     */
    @Override
    public List<Pair> matchPlayersInDivisionWeighted(long divisionId) {
        List<User> signedInUsers = getSignedInUsersInDivision(divisionId);
        if (signedInUsers.size() < 2) {
            throw new IllegalStateException("Not enough players signed in for matchmaking in division " + divisionId);
        }

        signedInUsers.sort(Comparator.comparingDouble(this::calculateWeightedScore).reversed());

        List<Pair> pairs = new ArrayList<>();
        Iterator<User> iterator = signedInUsers.iterator();
        User previous = null;

        while (iterator.hasNext()) {
            User current = iterator.next();
            if (previous == null) {
                previous = current;
            } else {
                pairs.add(new Pair(previous.getId(), current.getId()));
                logger.info("Matched players (weighted): user1={}, user2={}", previous.getId(), current.getId());

                previous = null;
            }
        }

        logger.info("Completed weighted matchmaking for division {}: {}", divisionId, pairs);
        return pairs;
    }

    /**
     * Matches players in a division optimally based on their weighted scores and compatibility.
     * This is the primary method used for player matching.
     *
     * @param divisionId the ID of the division
     * @return a list of optimally matched pairs of users
     */
    @Override
    public List<Pair> matchPlayersOptimally(Long divisionId) {
        List<User> signedInUsers = getSignedInUsersInDivision(divisionId);
        signedInUsers.sort(Comparator.comparingDouble(this::calculateWeightedScore));
        List<Pair> pairs = new ArrayList<>();
        boolean[] used = new boolean[signedInUsers.size()];

        for (int i = 0; i < signedInUsers.size(); i++) {
            if (!used[i]) {
                double minDifference = Double.MAX_VALUE;
                int bestMatchIndex = -1;

                for (int j = i + 1; j < signedInUsers.size(); j++) {
                    if (!used[j]) {
                        double difference = Math.abs(calculateWeightedScore(signedInUsers.get(i)) - calculateWeightedScore(signedInUsers.get(j)));
                        difference += calculateCompatibilityScore(signedInUsers.get(i).getId(), signedInUsers.get(j).getId());
                        if (difference < minDifference) {
                            minDifference = difference;
                            bestMatchIndex = j;
                        }
                    }
                }

                if (bestMatchIndex != -1) {
                    pairs.add(new Pair(signedInUsers.get(i).getId(), signedInUsers.get(bestMatchIndex).getId()));
                    used[i] = true;
                    used[bestMatchIndex] = true;
                    logger.info("Optimally matched players: user1={}, user2={}", signedInUsers.get(i).getId(), signedInUsers.get(bestMatchIndex).getId());

                }
            }
        }
        logger.info("Completed optimal matchmaking for division {}: {}", divisionId, pairs);
        return pairs;
    }

    /**
     * Calculates the weighted score for a user based on their ELO rating, match counter, and recent performance.
     *
     * @param user the user for whom to calculate the weighted score
     * @return the weighted score for the user
     */
    private double calculateWeightedScore(User user) {
        int recentPerformance = matchService.calculateRecentPerformance(user.getId());
        double weightedScore = 0.6 * user.getEloRating() + 0.2 * user.getMatchCounter() + 0.2 * recentPerformance;
        logger.info("Calculated weighted score for user {}: weightedScore={}", user.getId(), weightedScore);
        return weightedScore;
    }

    /**
     * Saves a division.
     *
     * @param division the division to save
     * @return the saved division
     */
    @Override
    public Division save(Division division) {
        Division savedDivision = divisionRepository.save(division);
        logger.info("Saved division: {}", savedDivision);
        return savedDivision;
    }

    /**
     * Calculates the compatibility score between two players based on their match history.
     *
     * @param player1Id the ID of the first player
     * @param player2Id the ID of the second player
     * @return the compatibility score between the two players
     */
    private double calculateCompatibilityScore(Long player1Id, Long player2Id) {
        long matchCount = matchService.getNumberOfMatchesBetweenPlayers(player1Id, player2Id);
        double compatibilityScore = matchCount * 200;
        logger.info("Calculated compatibility score between players {} and {}: {}", player1Id, player2Id, compatibilityScore);
        return compatibilityScore;
    }

    /**
     * Retrieves all divisions in a league.
     *
     * @param leagueId the ID of the league
     * @return a list of divisions in the league
     */
    @Override
    public List<Division> getAllDivisionsInLeague(Long leagueId) {
        List<Division> divisions = divisionRepository.findByLeagueId(leagueId);
        logger.info("Fetched divisions for league {}: {}", leagueId, divisions);
        return divisions;    }

}

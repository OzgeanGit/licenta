package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.League;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.LeagueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.matchmaking.elo.licenta.model.User.DEFAULT_ELO_RATING;

/**
 * Service implementation for managing leagues.
 * This service provides functionality for creating, retrieving, updating, and deleting leagues,
 * as well as handling related operations such as player distribution and end-of-season processing.
 */
@Service
public class LeagueServiceImpl implements LeagueService {

    private static final Logger logger = LoggerFactory.getLogger(LeagueServiceImpl.class);

    private final LeagueRepository leagueRepository;
    private final UserService userService;
    private final DivisionService divisionService;


    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository,
                             UserService userService, DivisionService divisionService) {
        this.leagueRepository = leagueRepository;
        this.userService = userService;
        this.divisionService = divisionService;
    }

    /**
     * Retrieves a league by its ID.
     *
     * @param leagueId the ID of the league
     * @return the league with the specified ID
     * @throws IllegalStateException if the league is not found
     */
    @Override
    public League getLeagueById(Long leagueId) {
        return leagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalStateException("League not found with id: " + leagueId));
    }

    /**
     * Creates a new league with the given name.
     *
     * @param name the name of the new league
     * @return the created league
     */
    @Override
    public League createLeague(String name) {
        League league = new League();
        league.setName(name);
        League createdLeague = leagueRepository.save(league);
        logger.info("Created new league: {}", createdLeague);
        return createdLeague;
    }

    /**
     * Retrieves a league by its ID.
     *
     * @param id the ID of the league
     * @return an Optional containing the league if found, or an empty Optional if not found
     */
    @Override
    public Optional<League> getLeague(Long id) {
        return leagueRepository.findById(id);
    }

    /**
     * Retrieves all leagues.
     *
     * @return a list of all leagues
     */
    @Override
    public List<League> getAllLeagues() {
        List<League> leagues = leagueRepository.findAll();
        logger.info("Fetched all leagues: {}", leagues);
        return leagues;
    }

    /**
     * Deletes a league by its ID.
     *
     * @param id the ID of the league to delete
     */
    @Override
    public void deleteLeague(Long id) {
        leagueRepository.deleteById(id);
        logger.info("Deleted league with id {}", id);
    }

    /**
     * Retrieves all divisions in a specified league.
     *
     * @param leagueId the ID of the league
     * @return a list of divisions in the league
     */
    @Override
    public List<Division> getAllDivisionsInLeague(Long leagueId) {
        return divisionService.getAllDivisionsInLeague(leagueId);
    }

    /**
     * Ends the season for a league, promoting and demoting players between divisions
     * and performing a soft reset of ELO ratings.
     *
     * @param leagueId the ID of the league
     */
    @Override
    public void endSeason(Long leagueId) {
        List<Division> divisions = divisionService.getAllDivisionsInLeague(leagueId);
        divisions.sort(Comparator.comparing(Division::getRank));

        logger.info("[START] End of season processing for league {} ", leagueId);


        for (int i = 0; i < divisions.size(); i++) {
            Division division = divisions.get(i);
            List<User> players = userService.getAllUsersInDivision(division.getId());
            players.sort(Comparator.comparing(User::getEloRating).reversed());
            int numberOfPlayersToChange = players.size() / 10;

            if (i > 0) {
                List<User> playersToPromote = players.subList(0, numberOfPlayersToChange);
                Division higherDivision = divisions.get(i - 1);
                promotePlayers(playersToPromote, higherDivision.getId());
                logger.info("Promoted players from division {} to division {}: {}", division.getId(), higherDivision.getId(), playersToPromote);

            }

            if (i < divisions.size() - 1) {
                List<User> playersToDemote = players.subList(players.size() - numberOfPlayersToChange, players.size());
                Division lowerDivision = divisions.get(i + 1);
                demotePlayers(playersToDemote, lowerDivision.getId());
                logger.info("Demoted players from division {} to division {}: {}", division.getId(), lowerDivision.getId(), playersToDemote);

            }
        }

        softResetEloOfUsersInLeague(leagueId);
        logger.info("Completed end of season processing for league {}", leagueId);

    }

    /**
     * Promotes a list of players to a new division.
     *
     * @param players the players to promote
     * @param newDivisionId the ID of the new division
     */
    private void promotePlayers(List<User> players, Long newDivisionId) {
        for (User player : players) {
            player.setDivisionId(newDivisionId);
            userService.save(player);
        }
    }

    /**
     * Demotes a list of players to a new division.
     *
     * @param players the players to demote
     * @param newDivisionId the ID of the new division
     */
    private void demotePlayers(List<User> players, Long newDivisionId) {
        for (User player : players) {
            player.setDivisionId(newDivisionId);
            userService.save(player);
        }
    }

    /**
     * Adds a player to a league.
     *
     * @param leagueId the ID of the league
     * @param userId the ID of the user to add to the league
     */
    @Override
    public void addPlayerToLeague(Long leagueId, Long userId) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

        user.setLeagueId(leagueId);
        userService.save(user);
        logger.info("Added player {} to league {}", userId, leagueId);
    }

    /**
     * Soft resets the ELO ratings of all users in a league.
     *
     * @param leagueId the ID of the league
     */
    public void softResetEloOfUsersInLeague(Long leagueId){
        List<User> users = userService.getAllUsersInLeague(leagueId);
        users.forEach(user -> user.setEloRating((user.getEloRating() + DEFAULT_ELO_RATING) / 2));
        logger.info("Soft reset ELO for users in league {}", leagueId);

    }

    /**
     * Removes a player from a league.
     *
     * @param leagueId the ID of the league
     * @param userId the ID of the user to remove from the league
     * @throws IllegalStateException if the user is not in the specified league
     */
    @Override
    public void removePlayerFromLeague(Long leagueId, Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

        if (!user.getLeagueId().equals(leagueId)) {
            throw new IllegalStateException("User is not in the specified league");
        }

        user.setLeagueId(null);
        userService.save(user);
    }

    /**
     * Distributes players in divisions within a league based on their ELO ratings.
     *
     * @param leagueId the ID of the league
     * @return true if the distribution is successful, false if there are no divisions in the league
     */
    @Override
    public boolean distributePlayersInDivisions(Long leagueId) {
        List<User> players = userService.getAllUsersInLeague(leagueId);
        List<Division> divisions = divisionService.getAllDivisionsInLeague(leagueId);

        if (divisions.isEmpty()) {
            logger.error("No divisions found in league {}", leagueId);
            return false; //sau exceptie
        }

        logger.info("[START] Player distribution in divisions for league {}", leagueId);

        players.sort(Comparator.comparing(User::getEloRating).reversed());

        int divisionSize = players.size() / divisions.size();
        int playerIndex = 0;
        for (Division division : divisions) {
            for (int i = 0; i < divisionSize && playerIndex < players.size(); i++) {
                User player = players.get(playerIndex++);
                player.setDivisionId(division.getId());
                userService.save(player);
            }
        }

        while (playerIndex < players.size()) {
            User player = players.get(playerIndex++);
            player.setDivisionId(divisions.get(divisions.size() - 1).getId());
            userService.save(player);
        }

        logger.info("[END] Player distribution in divisions for league {}", leagueId);
        return true;
    }

    /**
     * Adds a new division to a league.
     *
     * @param leagueId the ID of the league
     * @param division the division to add
     * @throws IllegalStateException if the league does not exist
     */
    @Override
    public void addDivisionToLeague(Long leagueId, Division division) {
        leagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalStateException("League not found with id: " + leagueId));

        int maxRank = divisionService.getAllDivisionsInLeague(leagueId)
                .stream()
                .mapToInt(Division::getRank)
                .max()
                .orElse(0);

        division.setLeagueId(leagueId);
        division.setRank(maxRank + 1);

        divisionService.save(division);
    }

    /**
     * Removes a division from a league.
     *
     * @param leagueId the ID of the league
     * @param divisionId the ID of the division to remove
     * @throws IllegalStateException if the division does not exist or is not in the specified league
     */
    @Override
    public void removeDivisionFromLeague(Long leagueId, Long divisionId) {
        Division division = divisionService.getDivision(divisionId)
                .orElseThrow(() -> new IllegalStateException("Division not found with id: " + divisionId));
        if (!division.getLeagueId().equals(leagueId)) {
            throw new IllegalStateException("Division is not in the specified league");
        }

        divisionService.delete(division);

        List<Division> remainingDivisions = divisionService.getAllDivisionsInLeague(leagueId)
                .stream()
                .sorted(Comparator.comparingInt(Division::getRank))
                .toList();

        int rank = 1;
        for (Division remainingDivision : remainingDivisions) {
            remainingDivision.setRank(rank++);
            divisionService.save(remainingDivision);
        }
    }

    /**
     * Saves a league.
     *
     * @param league the league to save
     * @return the saved league
     */
    @Override
    public League save(League league) {
        League savedLeague = leagueRepository.save(league);
        logger.info("Saved league: {}", savedLeague);
        return savedLeague;
    }
}

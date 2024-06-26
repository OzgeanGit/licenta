package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.matchmaking.elo.licenta.model.User.DEFAULT_ELO_RATING;

/**
 * Service implementation for managing users.
 * This service provides functionality for creating, retrieving, updating, and deleting users,
 * as well as handling user sign-in, sign-out, ELO rating updates, and activity tracking.
 */
@Lazy(false)
@Service
public class UserServiceImpl implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public final int K_FACTOR_CONSTANT = 32;

    private final UserRepository userRepository;



    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the list of signed-in users.
     *
     * @return the list of signed-in users
     */
    @Override
    public List<User> getSignedInUsers() {
       return userRepository.findBySignedInTrue();
    }

    /**
     * Updates a user's information.
     *
     * @param id           the ID of the user
     * @param updatedUser  the updated user
     * @return the updated user or null if the user does not exist
     */

    @Override
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEloRating(updatedUser.getEloRating());
            user.setName(updatedUser.getName());
            user.setSignedIn(updatedUser.isSignedIn());

            logger.info("Updated user with ID {}: {}", id, user);
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * Finds a user by ID.
     *
     * @param id the ID of the user
     * @return the found user or an empty Optional if the user does not exist
     */

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }


    /**
     * Saves a new or updated user.
     *
     * @param user the user to save
     * @return the saved user
     */
    @Override
    public User save(User user) {
        User savedUser = userRepository.save(user);
        logger.info("Saved new user: {}", savedUser);
        return savedUser;
    }


    /**
     * Finds users by league ID.
     *
     * @param leagueId the ID of the league
     * @return the list of users in the specified league
     */
    @Override
    public List<User> findByLeagueId(Long leagueId) {
        return userRepository.findByLeagueId(leagueId);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user
     * @return true if the user was deleted, otherwise false
     */
    @Override
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            logger.info("Deleted user with ID {}", id);
            return true;
        }
        return false;
    }

    /**
     * Updates a player's ELO rating.
     *
     * @param player the player to update
     * @param newElo the new ELO rating
     */
    @Override
    public void updatePlayerElo(User player, int newElo) {
        logger.info("Updating ELO for player {}: currentElo={}, newElo={}", player.getId(), player.getEloRating(), newElo);
        player.setEloRating(newElo);
        userRepository.save(player);
        logger.info("Updated ELO for player {}: new ELO = {}", player.getId(), newElo);

    }

    /**
     * Updates a player's last activity date.
     *
     * @param player the player to update
     */
    @Override
    public void updatePlayerActivityDate(User player) {
        player.setLastActiveDate(LocalDate.now());
        userRepository.save(player);
        logger.info("Updated activity date for player {}: lastActiveDate = {}", player.getId(), LocalDate.now());

    }


    /**
     * Signs in a user.
     *
     * @param id the ID of the user
     * @return true if the sign-in was successful, otherwise false
     */
    @Override
    public boolean signIn(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSignedIn(true);
            user.setLastActiveDate(LocalDate.now());
            userRepository.save(user);
            logger.info("Signed in user with ID {}", id);

            return true;
        }
        return false;
    }


    /**
     * Signs out a user.
     *
     * @param id the ID of the user
     * @return true if the sign-out was successful, otherwise false
     */
    @Override
    public boolean signOut(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSignedIn(false);
            userRepository.save(user);
            logger.info("Signed out user with ID {}", id);

            return true;
        }
        return false;
    }

    /**
     * Retrieves the list of signed-in users in a division.
     *
     * @param divisionId the ID of the division
     * @return the list of signed-in users in the specified division
     */
    @Override
    public List<User> getSignedInUsersInDivision(long divisionId) {
        return userRepository.findByDivisionIdAndSignedInTrue(divisionId);
    }


    /**
     * Applies ELO decay every week for inactive users.
     */
    @Scheduled(cron = "0 0 0 * * SUN")
    public void applyEloDecay() {
        logger.info("Running applyEloDecay...");
        List<User> allUsers = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : allUsers) {
            if (user.getLastActiveDate() != null && ChronoUnit.DAYS.between(user.getLastActiveDate(), today) > 7) {
                int newElo = (int) (user.getEloRating() * 0.99); // reduce ELO cu 1%
                user.setEloRating(newElo);
                userRepository.save(user);
                logger.info("Applied ELO decay for user {}: new ELO = {}", user.getId(), newElo);
            }
        }
    }


    /**
     * Resets all users' ELO ratings to a preset value.
     */
    public void hardResetElo() {
        List<User> allUsers = userRepository.findAll();

        logger.info("[START]Hard reset ELO for all users");
        for (User user : allUsers) {
            user.setEloRating(1500); // reseteaza elo-ul tuturor la o valoare prestabilita
            userRepository.save(user);

        }
        logger.info("[END]Hard reset ELO for all users");

    }


    /**
     * Partially resets all users' ELO ratings at the beginning of each month (variable by context).
     */
    @Scheduled(cron = "0 0 0 1 1/1 ?") // in fiecare zi a fiecarei luni
    public void softResetElo() {
        List<User> allUsers = userRepository.findAll();

        logger.info("[START]Soft reset ELO for all users");
        for (User user : allUsers) {
            int currentElo = user.getEloRating();
            int newElo = (currentElo + DEFAULT_ELO_RATING) / 2; // aduce punctajul elo la o valoare mai aproape de medie
            user.setEloRating(newElo);
            userRepository.save(user);
        }
        logger.info("[END]Soft reset ELO for all users");

    }


    @Override
    public Long getUserByName(String username) {
        User user = userRepository.findUserByName(username);
        return user.getId();
    }

    @Override
    public List<User> findUsersInDivisionSortedByEloDescending(Long divisionId) {
        return userRepository.findByDivisionIdOrderByEloRatingDesc(divisionId);
    }

    @Override
    public List<User> getAllSignedInUsersInDivision(long divisionId) {
        return userRepository.findByDivisionIdAndSignedInTrue(divisionId);
    }

    @Override
    public List<User> getAllUsersInDivision(Long divisionId) {
        return userRepository.findByDivisionId(divisionId);
    }

    @Override
    public List<User> getAllUsersInLeague(Long leagueId){
        return userRepository.findByLeagueId(leagueId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

}

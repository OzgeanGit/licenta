package com.matchmaking.elo.licenta.service;

import com.matchmaking.elo.licenta.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing users.
 */
@Service
public interface UserService {

    List<User> getSignedInUsers();

    User updateUser(Long id, User user);

    Optional<User> findUserById(Long id);

    User save(User user);

    List<User> findByLeagueId(Long leagueId);

    boolean deleteUser(Long id);

    void updatePlayerElo(User player, int newElo);

    void updatePlayerActivityDate(User player);

    boolean signIn(long id);

    boolean signOut(long id);
    List<User> getSignedInUsersInDivision(long divisionId);



    Long getUserByName(String username);

    List<User> findUsersInDivisionSortedByEloDescending(Long divisionId);

    List<User> getAllSignedInUsersInDivision(long divisionId);

    List<User> getAllUsersInDivision(Long divisionId);

    List<User> getAllUsersInLeague(Long leagueId);

    List<User> getAllUsers();


    Optional<User> getUserById(Long id);
}

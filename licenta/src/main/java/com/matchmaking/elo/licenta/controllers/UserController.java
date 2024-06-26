package com.matchmaking.elo.licenta.controllers;

import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.service.MatchService;
import com.matchmaking.elo.licenta.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Controller for handling user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    private final MatchService matchService;

    /**
     * Constructs a UserController with the specified UserService and MatchService.
     *
     * @param userService the service for managing users
     * @param matchService the service for managing matches
     */
    public UserController(UserService userService, MatchService matchService) {
        this.userService = userService;
        this.matchService = matchService;
    }

    /**
     * Gets all users.
     *
     * @return the list of all users
     */
    @GetMapping("/get/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Gets all signed-in users.
     *
     * @return the list of all signed-in users
     */
    @GetMapping("/get/all/signedin")
    public ResponseEntity<List<User>> getAllSignedInUsers() {
        List<User> users = userService.getSignedInUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Gets a user by their ID.
     *
     * @param id the ID of the user
     * @return the user with the specified ID, or a not found response if the user does not exist
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.save(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Generates a specified number of users with random ELO ratings.
     *
     * @param numUsers the number of users to generate
     * @return a message indicating the number of users generated
     */
    @PostMapping("/generateUsers/{numUsers}")
    public ResponseEntity<String> generateUsers(@PathVariable int numUsers) {
        Random random = new Random();
        for (int i = 1; i <= numUsers; i++) {
            User user = new User();
            user.setName("User" + i);
            user.setEloRating(random.nextInt(1100,2100));
            userService.save(user);
        }
        return ResponseEntity.ok("Generated " + numUsers + " users.");
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param user the updated user data
     * @return the updated user, or a not found response if the user does not exist
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a response entity indicating the result of the operation
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Signs in a user by their ID.
     *
     * @param id the ID of the user to sign in
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("/signin/{id}")
    public ResponseEntity<User> signInUser(@PathVariable Long id) {
        boolean signedIn = userService.signIn(id);
        if (signedIn) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Signs out a user by their ID.
     *
     * @param id the ID of the user to sign out
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("/signout/{id}")
    public ResponseEntity<User> signOutUser(@PathVariable Long id) {
        boolean signedOut = userService.signOut(id);
        if (signedOut) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets the number of wins for a user.
     *
     * @param userId the ID of the user
     * @return the number of wins for the specified user
     */
    @GetMapping("/get/wins/{userId}")
    public ResponseEntity<Long> getUserWins(@PathVariable Long userId) {
        return ResponseEntity.ok(matchService.getNumberOfPlayerWins(userId));
    }

    /**
     * Gets the number of losses for a user.
     *
     * @param userId the ID of the user
     * @return the number of losses for the specified user
     */
    @GetMapping("/get/losses/{userId}")
    public ResponseEntity<Long> getUserLosses(@PathVariable Long userId) {
        return ResponseEntity.ok(matchService.getNumberOfPlayerLosses(userId));
    }

    /**
     * Gets the ID of a user by their username.
     *
     * @param username the username of the user
     * @return the ID of the user with the specified username
     */
    @GetMapping("/get/id/{username}")
    public ResponseEntity<Long> getUserIdByName(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByName(username));
    }

}

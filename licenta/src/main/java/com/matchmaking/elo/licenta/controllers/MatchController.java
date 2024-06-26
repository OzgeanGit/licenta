package com.matchmaking.elo.licenta.controllers;

import com.matchmaking.elo.licenta.model.Match;
import com.matchmaking.elo.licenta.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling match-related operations.
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {


    private final MatchService matchService;

    /**
     * Constructs a MatchController with the specified MatchService.
     *
     * @param matchService the service for managing matches
     */
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Creates a new match.
     *
     * @param match the match to create
     * @param player1Score the score of player 1
     * @param player2Score the score of player 2
     * @return the created match
     */
    @PostMapping("/create")
    public ResponseEntity<Match> createMatch(@RequestBody Match match, @RequestParam int player1Score, @RequestParam int player2Score) {
        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        match.setWinnerAndLoser();
        match.setMatchDateTime(LocalDateTime.now());
        matchService.updatePlayersEloRating(match);

        Match createdMatch = matchService.saveMatch(match);
        return ResponseEntity.ok(createdMatch);
    }

    /**
     * Deletes a match by its ID.
     *
     * @param id the ID of the match to delete
     * @return a response entity indicating the result of the operation
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        boolean deleted = matchService.deleteMatch(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets all matches.
     *
     * @return the list of all matches
     */
    @GetMapping("/get/all")
    public ResponseEntity<List<Match>> getMatches() {
        List<Match> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    /**
     * Gets a match by its ID.
     *
     * @param id the ID of the match
     * @return the match with the specified ID, or a not found response if the match does not exist
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Match> getMatchesById(@PathVariable Long id) {
        Optional<Match> optionalMatch = matchService.getMatchById(id);
        return optionalMatch.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Gets matches by user ID.
     *
     * @param userId the ID of the user
     * @return the list of matches involving the specified user
     */
    @GetMapping("/get/matches/user/{userId}")
    public ResponseEntity<List<Match>> getMatchesByUserId(@PathVariable Long userId) {
        List<Match> matches = matchService.getMatchesByUserId(userId);
        return ResponseEntity.ok(matches);
    }
}

package com.matchmaking.elo.licenta.controllers;

import com.matchmaking.elo.licenta.model.Division;
import com.matchmaking.elo.licenta.model.Pair;
import com.matchmaking.elo.licenta.model.User;
import com.matchmaking.elo.licenta.service.DivisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling division-related operations.
 */
@RestController
@RequestMapping("/api/division")
public class DivisionController {

    private final DivisionService divisionService;

    /**
     * Constructs a DivisionController with the specified DivisionService.
     *
     * @param divisionService the service for managing divisions
     */
    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    /**
     * Creates a new division.
     *
     * @param division the division to create
     * @return the created division
     */
    @PostMapping("/create")
    public ResponseEntity<Division> createDivision(@RequestBody Division division) {
        Division createdDivision = divisionService.save(division);
        return ResponseEntity.ok(createdDivision);
    }

    /**
     * Creates a new division within an existing league.
     *
     * @param newDivision the division to create
     * @param leagueId the ID of the league
     * @return the created division
     */
    @PostMapping("/create/{leagueId}")
    public ResponseEntity<Division> createDivisionInExistingLeague(@RequestBody Division newDivision, @PathVariable Long leagueId) {
        List<Division> existingDivisions = divisionService.getAllDivisionsInLeague(leagueId);
        for (Division division : existingDivisions) {
            if (division.getRank() >= newDivision.getRank()) {
                division.setRank(division.getRank() + 1);
                divisionService.save(division);
            }
        }

        newDivision.setLeagueId(leagueId);
        divisionService.save(newDivision);
        return ResponseEntity.ok(newDivision);
    }

    /**
     * Matches players optimally within a division.
     *
     * @param divisionId the ID of the division
     * @return the list of matched pairs
     */
    @PostMapping("/pair/{divisionId}")
    public ResponseEntity<?> matchPlayersInDivision(@PathVariable long divisionId) {
        try {
            List<Pair> pairs = divisionService.matchPlayersOptimally(divisionId);
            return ResponseEntity.ok(pairs);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gets pairs of players in a division.
     *
     * @param divisionId the ID of the division
     * @return the list of pairs
     */
    @GetMapping("/get/pairs/{divisionId}")
    public ResponseEntity<List<Pair>> getPairsInDivision(@PathVariable long divisionId) {
        List<Pair> pairs = divisionService.matchPlayersInDivision(divisionId);
        return ResponseEntity.ok(pairs);
    }

    /**
     * Matches players within a division using a weighted algorithm.
     *
     * @param divisionId the ID of the division
     * @return the list of weighted pairs
     */
    @PostMapping("pair/weighted/{divisionId}")
    public ResponseEntity<List<Pair>> getWeightedPairsInDivision(@PathVariable long divisionId) {
        List<Pair> pairs = divisionService.matchPlayersInDivisionWeighted(divisionId);
        return ResponseEntity.ok(pairs);
    }

    /**
     * Gets the standings of a division.
     *
     * @param divisionId the ID of the division
     * @return the list of users sorted by ELO
     */
    @GetMapping("/get/standings/{divisionId}")
    public ResponseEntity<List<User>> getDivisionStandings(@PathVariable long divisionId) {
        List<User> standings = divisionService.getDivisionStandings(divisionId);
        return ResponseEntity.ok(standings);
    }

    /**
     * Gets all signed-in players in a division.
     *
     * @param divisionId the ID of the division
     * @return the list of signed-in players
     */
    @GetMapping("/get/active/players/{divisionId}")
    public ResponseEntity<List<User>> getAllSignedInPlayersInDivision(@PathVariable long divisionId) {
        List<User> standings = divisionService.getSignedInUsersInDivision(divisionId);
        return ResponseEntity.ok(standings);
    }


}

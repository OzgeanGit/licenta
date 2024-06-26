package com.matchmaking.elo.licenta.controllers;

import com.matchmaking.elo.licenta.model.*;
import com.matchmaking.elo.licenta.service.DivisionService;
import com.matchmaking.elo.licenta.service.LeagueService;
import com.matchmaking.elo.licenta.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Controller for handling league-related operations.
 */
@RestController
@RequestMapping("/api/league")
public class LeagueController {

    private final LeagueService leagueService;

    private final DivisionService divisionService;
    private final UserService userService;

    private final RestTemplate restTemplate;


    /**
     * Constructs a LeagueController with the specified services.
     *
     * @param leagueService   the service for managing leagues
     * @param divisionService the service for managing divisions
     * @param userService     the service for managing users
     */
    public LeagueController(LeagueService leagueService, DivisionService divisionService, UserService userService, RestTemplate restTemplate) {
        this.leagueService = leagueService;
        this.divisionService = divisionService;
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves all leagues.
     *
     * @return a list of all leagues
     */
    @GetMapping("/get/all")
    public ResponseEntity<List<League>> getAllLeagues() {
        List<League> allLeagues = leagueService.getAllLeagues();
        return ResponseEntity.ok(allLeagues);
    }

    /**
     * Retrieves all divisions in a specified league.
     *
     * @param leagueId the ID of the league
     * @return a list of divisions in the league
     */
    @GetMapping("/get/divisions/{leagueId}")
    public ResponseEntity<List<Division>> getAllDivisionsInLeague(@PathVariable long leagueId) {
        List<Division> allDivisionsInLeague = divisionService.getAllDivisionsInLeague(leagueId);
        return ResponseEntity.ok(allDivisionsInLeague);
    }

    /**
     * Creates a new league.
     *
     * @param league the league to create
     * @return the created league
     */
    @PostMapping("/create")
    public ResponseEntity<League> createLeague(@RequestBody League league) {
        League createdLeague = leagueService.save(league);
        return ResponseEntity.ok(createdLeague);
    }

    /**
     * Adds a division to a specified league.
     *
     * @param division the division to add
     * @param leagueId the ID of the league
     * @return the added division
     */
    @PostMapping("/add/division/{leagueId}")
    public ResponseEntity<Division> addDivision(@RequestBody Division division, @PathVariable long leagueId) {
        leagueService.addDivisionToLeague(leagueId, division);
        return ResponseEntity.ok(division);
    }

    /**
     * Removes a division from a specified league.
     *
     * @param leagueId the ID of the league
     * @param divisionId the ID of the division to remove
     * @return a response indicating success or failure
     */
    @DeleteMapping("/remove/division/{leagueId}/{divisionId}")
    public ResponseEntity<String> removeDivision(@PathVariable Long leagueId, @PathVariable Long divisionId) {
        leagueService.removeDivisionFromLeague(leagueId, divisionId);
        return ResponseEntity.ok("Division with ID " + divisionId + " was successfully removed.");
    }

    /**
     * Distributes players in a specified league into divisions.
     *
     * @param leagueId the ID of the league
     * @return a response indicating success or failure
     */
    @PostMapping("/distribute/{leagueId}")
    public ResponseEntity<String> distributePlayers(@PathVariable long leagueId) {
        boolean distributionResult = leagueService.distributePlayersInDivisions(leagueId);
        if (distributionResult) {
            return ResponseEntity.ok("Players have been successfully distributed in divisions.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to distribute players.");
        }
    }

    /**
     * Adds a player to a specified league.
     *
     * @param leagueId the ID of the league
     * @param userId the ID of the player to add
     * @return the added player
     */
    @PostMapping("/add/player/{leagueId}/{userId}")
    public ResponseEntity<User> addPlayerToLeague(@PathVariable long leagueId, @PathVariable long userId) {
        Optional<User> playerOpt = userService.findUserById(userId);
        if (playerOpt.isPresent()) {
            User player = playerOpt.get();
            player.setLeagueId(leagueId);
            userService.save(player);
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds multiple players to a specified league.
     *
     * @param leagueId the ID of the league
     * @param userIds the IDs of the players to add
     * @return a list of added players
     */
    @PostMapping("/add/players/{leagueId}")
    public ResponseEntity<List<User>> addPlayersToLeague(@PathVariable long leagueId, @RequestBody List<Long> userIds) {
        League league = leagueService.getLeagueById(leagueId);
        List<User> addedPlayers = new ArrayList<>();

        for (Long userId : userIds) {
            Optional<User> playerOpt = userService.findUserById(userId);
            if (playerOpt.isPresent()) {
                User player = playerOpt.get();
                leagueService.addPlayerToLeague(league.getId(), player.getId());
                addedPlayers.add(player);
            }
        }

        return ResponseEntity.ok(addedPlayers);
    }

    /**
     * Retrieves all players in a specified league.
     *
     * @param leagueId the ID of the league
     * @return a list of players in the league
     */
    @GetMapping("/get/players/{leagueId}")
    public ResponseEntity<List<User>> getPlayers(@PathVariable Long leagueId) {
        List<User> playersInLeague = userService.findByLeagueId(leagueId);
        return ResponseEntity.ok(playersInLeague);
    }


    /**
     * Ends the current season for a specified league.
     *
     * @param leagueId the ID of the league
     * @return the league after ending the season
     */
    @PostMapping("/end/season/{leagueId}")
    public ResponseEntity<League> endSeason(@PathVariable long leagueId) {
        leagueService.endSeason(leagueId);
        return ResponseEntity.ok(leagueService.getLeagueById(leagueId));
    }

    @PostMapping("/generate-matches/{divisionId}")
    public ResponseEntity<String> generateMatches(@PathVariable long divisionId, @RequestParam int rounds) {
        Random random = new Random();

        for (int round = 0; round < rounds; round++) {
            ResponseEntity<Pair[]> response = restTemplate.getForEntity("http://localhost:8088/api/division/get/pairs/" + divisionId, Pair[].class);
            List<Pair> pairs = List.of(response.getBody());

            for (Pair pair : pairs) {
                int player1Score = random.nextInt(100);
                int player2Score = random.nextInt(100);

                Match match = new Match();
                match.setPlayer1Id(pair.getUser1Id());
                match.setPlayer2Id(pair.getUser2Id());
                match.setPlayer1Score(player1Score);
                match.setPlayer2Score(player2Score);
                match.setWinnerAndLoser();
                match.setMatchDateTime(LocalDateTime.now());

                restTemplate.postForEntity("http://localhost:8088/api/match/create?player1Score=" + player1Score + "&player2Score=" + player2Score, match, Match.class);
            }

            System.out.println("Round " + (round + 1) + " completed.");
        }
        return ResponseEntity.ok("Matches generated for " + rounds + " rounds.");
    }
}


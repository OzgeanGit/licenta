package com.matchmaking.elo.licenta.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a user in the matchmaking system.
 */
@Entity
@Table(name = "USERS")
public class User {
    public static final int DEFAULT_ELO_RATING = 1500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MATCH_COUNTER")
    long matchCounter;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ELO_RATING")
    private int eloRating;

    @Column(name = "LAST_ACTIVE_DATE")
    private LocalDate lastActiveDate;

    @Column(name = "DIVISION_ID")
    private Long divisionId;

    @Column(name = "LEAGUE_ID")
    private Long leagueId;

    @Column(name = "SIGNED_ID")
    private boolean signedIn;


    public User() {
    }

    public User(Long id) {
        this.id = id;
        this.matchCounter = 0;
        this.lastActiveDate = LocalDate.now();
    }

    public User(long id, int eloRating) {
        this.id = id;
        this.eloRating = eloRating;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMatchCounter() {
        return matchCounter;
    }

    public void setMatchCounter(long matchCounter) {
        this.matchCounter = matchCounter;
    }

    public int getEloRating() {
        return eloRating;
    }

    public void setEloRating(int eloRating) {
        this.eloRating = eloRating;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signIn) {
        this.signedIn = signIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return eloRating == user.eloRating && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eloRating);
    }

    /**
     * Metodă ajutătoare pentru a crea un User.
     * @param id ID-ul utilizatorului.
     * @param name Numele utilizatorului.
     * @param eloRating Ratingul ELO al utilizatorului.
     * @param lastActiveDate Ultima dată activă a utilizatorului.
     * @param divisionId ID-ul diviziei utilizatorului.
     * @param leagueId ID-ul ligii utilizatorului.
     * @param signedIn Starea de conectare a utilizatorului.
     * @return Un obiect User configurat.
     */
    public static User createUser(Long id, String name, int eloRating, LocalDate lastActiveDate,
                                  Long divisionId, Long leagueId, boolean signedIn) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEloRating(eloRating);
        user.setLastActiveDate(lastActiveDate);
        user.setDivisionId(divisionId);
        user.setLeagueId(leagueId);
        user.setSignedIn(signedIn);
        return user;
    }
}

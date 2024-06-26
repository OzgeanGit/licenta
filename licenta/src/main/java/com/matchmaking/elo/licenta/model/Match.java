package com.matchmaking.elo.licenta.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a match in the matchmaking system.
 */
@Entity
@Table(name = "MATCHES")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "PLAYER_1_ID")
    private long player1Id;
    @Column(name = "PLAYER_2_ID")
    private long player2Id;
    @Column(name = "PLAYER_1_ELO_AT_MATCH_TIME")
    private int player1EloAtMatchTime;
    @Column(name = "PLAYER_2_ELO_AT_MATCH_TIME")
    private int player2EloAtMatchTime;
    @Column(name = "PLAYER_1_SCORE")
    private int player1Score;
    @Column(name = "PLAYER_2_SCORE")
    private int player2Score;

    @Column(name = "WINNER_ID")
    private Long winnerId;

    @Column(name = "LOSER_ID")
    private Long loserId;

    @Column(name = "PLAYER_1_ELO_AFTER_MATCH")
    private int player1EloAfterMatch;

    @Column(name = "PLAYER_2_ELO_AFTER_MATCH")
    private int player2EloAfterMatch;

    @Column(name = "MATCH_DATE_TIME")
    private LocalDateTime matchDateTime;

    public Match(Long player1Id, Long player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.matchDateTime = LocalDateTime.now();
    }

    public Match() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public int getPlayer1EloAtMatchTime() {
        return player1EloAtMatchTime;
    }

    public void setPlayer1EloAtMatchTime(int player1EloAtMatchTime) {
        this.player1EloAtMatchTime = player1EloAtMatchTime;
    }

    public int getPlayer2EloAtMatchTime() {
        return player2EloAtMatchTime;
    }

    public void setPlayer2EloAtMatchTime(int player2EloAtMatchTime) {
        this.player2EloAtMatchTime = player2EloAtMatchTime;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Long getLoserId() {
        return loserId;
    }

    public void setLoserId(Long loserId) {
        this.loserId = loserId;
    }

    public int getPlayer1EloAfterMatch() {
        return player1EloAfterMatch;
    }

    public void setPlayer1EloAfterMatch(int player1EloAfterMatch) {
        this.player1EloAfterMatch = player1EloAfterMatch;
    }

    public int getPlayer2EloAfterMatch() {
        return player2EloAfterMatch;
    }

    public void setPlayer2EloAfterMatch(int player2EloAfterMatch) {
        this.player2EloAfterMatch = player2EloAfterMatch;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public void setWinnerAndLoser() {
        if (player1Score > player2Score) {
            winnerId = player1Id;
            loserId = player2Id;
        } else {
            winnerId = player2Id;
            loserId = player1Id;
        }
    }

    @Override
    public String toString() {
        return "Match " + id + "{" +
                "player1Id:" + player1Id +
                ", player2Id:" + player2Id +
                ", winnerId:" + winnerId +
                ", player1Score:" + player1Score +
                ", player2Score:" + player2Score +
                ", loserId:" + loserId +
                ", matchDateTime:" + matchDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return id == match.id &&
                player1Score == match.player1Score &&
                player2Score == match.player2Score &&
                player1Id == match.player1Id &&
                player2Id == match.player2Id &&
                winnerId.equals(match.winnerId) &&
                Objects.equals(loserId, match.loserId) &&
                matchDateTime.equals(match.matchDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, player1Id, player2Id, winnerId, loserId, player1Score, player2Score, matchDateTime);
    }
}

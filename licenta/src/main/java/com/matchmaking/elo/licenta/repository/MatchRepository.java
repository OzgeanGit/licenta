package com.matchmaking.elo.licenta.repository;

import com.matchmaking.elo.licenta.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing match data.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Long countByWinnerId(Long userId);
    Long countByPlayer1IdAndPlayer2Id(Long user1Id, Long user2Id);

    Long countByLoserId(Long userId);

}
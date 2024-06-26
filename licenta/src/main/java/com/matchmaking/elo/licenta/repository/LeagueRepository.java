package com.matchmaking.elo.licenta.repository;

import com.matchmaking.elo.licenta.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing league data.
 */
@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

}

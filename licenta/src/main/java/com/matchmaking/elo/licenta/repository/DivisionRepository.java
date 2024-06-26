package com.matchmaking.elo.licenta.repository;

import com.matchmaking.elo.licenta.model.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing division data.
 */
@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    List<Division> findByLeagueId(long leagueId);

}

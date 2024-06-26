package com.matchmaking.elo.licenta.repository;

import com.matchmaking.elo.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing user data.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLeagueId(Long leagueId);
    List<User> findByDivisionId(Long divisionId);
    List<User> findByDivisionIdOrderByEloRatingDesc(Long divisionId);

    List<User> findByDivisionIdAndSignedInTrue(long divisionId);

    List<User> findBySignedInTrue();

    User findUserByName(String name);
}

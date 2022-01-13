package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.gameOriented.GameRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRequirementsRepository extends JpaRepository<GameRequirements, Long> {
    Optional<GameRequirements> findGameRequirementsByGame_gameID(long gameID);
}

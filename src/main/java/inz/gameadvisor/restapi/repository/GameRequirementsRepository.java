package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.gameOriented.GameRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRequirementsRepository extends JpaRepository<GameRequirements, Long> {
    List<GameRequirements> findGameRequirementsByGame_gameID(long gameID);
}

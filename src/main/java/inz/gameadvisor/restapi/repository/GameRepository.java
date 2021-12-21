package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game,Long> {
    List<Game> findByNameContaining(String name);
}

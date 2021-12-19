package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Long> {
}

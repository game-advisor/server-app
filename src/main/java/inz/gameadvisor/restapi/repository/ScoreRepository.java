package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}

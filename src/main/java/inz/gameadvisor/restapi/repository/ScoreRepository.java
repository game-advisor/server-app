package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.reviewOriented.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
}

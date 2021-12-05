package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

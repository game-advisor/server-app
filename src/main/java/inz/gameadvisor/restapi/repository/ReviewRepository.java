package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.reviewOriented.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsBygameID(long gameID);
}

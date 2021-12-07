package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.Review;
import inz.gameadvisor.restapi.model.Score;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;

    public List<Review> getAllReviews(){
        if(reviewRepository.findAll().isEmpty())
            throw new CustomRepsonses.MyNotFoundException("No elements found");
        else
            return reviewRepository.findAll();
    }
}

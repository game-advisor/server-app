package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.reviewOriented.AddReview;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.model.reviewOriented.Score;
import inz.gameadvisor.restapi.repository.GameRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService extends CustomFunctions {

    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;
    private final GameRepository gameRepository;

    public ResponseEntity<Object> getAllReviewsForGame(long gameID, HttpServletRequest request){
        Optional<Game> game = gameRepository.findById(gameID);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        if(reviewRepository.findReviewsBygame(game.get()).isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No reviews found for the game");
        }

        return new ResponseEntity<>(reviewRepository.findReviewsBygame(game.get()), HttpStatus.OK);
    }

    public ResponseEntity<Object> createReviewForGame(long gameID,
                                                      AddReview addReview,
                                                      HttpServletRequest request,
                                                      String token){
        Optional<Game> game = gameRepository.findById(gameID);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        long userID = getUserIDFromToken(token);
        Review review = new Review();
        Score score = new Score();

        if(addReview.getContent().isBlank()){
            responseFromServer(HttpStatus.BAD_REQUEST,request,"Content cannot be empty");
        }
        if(score.getGameplayRating() <=0 || score.getMusicRating() <=0 || score.getGraphicsRating() <= 0){
            responseFromServer(HttpStatus.BAD_REQUEST,request,"Scores cannot be less than 0!");
        }

        score.setAvgFPS(addReview.getAvgFPS());
        score.setMusicRating(addReview.getMusicRating());
        score.setGameplayRating(addReview.getGameplayRating());
        score.setGraphicsRating(addReview.getGraphicsRating());
        scoreRepository.save(score);

        review.setReviewUserID(userID);
        review.setGame(game.get());
        review.setContent(addReview.getContent());
        review.setScore(score);
        long currentDateTime = System.currentTimeMillis();
        Timestamp resultDate = new Timestamp(currentDateTime);
        review.setDateCreated(resultDate);

        reviewRepository.save(review);
        return responseFromServer(HttpStatus.OK,request,"Review posted successfully");
    }
}

package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.reviewOriented.EditAddReview;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.model.reviewOriented.Score;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.GameRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.ScoreRepository;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService extends CustomFunctions {

    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

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

    public ResponseEntity<Object> addReview(long gameID, EditAddReview editAddReview, HttpServletRequest request, String token){
        Optional<Game> game = gameRepository.findById(gameID);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        Optional<User> user = userRepository.findById(getUserIDFromToken(token));
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }
        Review review = new Review();
        Score score = new Score();

        if(editAddReview.getContent().isBlank()){
            responseFromServer(HttpStatus.BAD_REQUEST,request,"Content cannot be empty");
        }
        if(score.getGameplayRating() <=0 || score.getMusicRating() <=0 || score.getGraphicsRating() <= 0){
            responseFromServer(HttpStatus.BAD_REQUEST,request,"Scores cannot be less than 0!");
        }

        score.setAvgFPS(editAddReview.getAvgFPS());
        score.setMusicRating(editAddReview.getMusicRating());
        score.setGameplayRating(editAddReview.getGameplayRating());
        score.setGraphicsRating(editAddReview.getGraphicsRating());
        scoreRepository.save(score);

        review.setReviewUser(user.get());
        review.setGame(game.get());
        review.setContent(editAddReview.getContent());
        review.setScore(score);
        long currentDateTime = System.currentTimeMillis();
        Timestamp resultDate = new Timestamp(currentDateTime);
        review.setDateCreated(resultDate);

        reviewRepository.save(review);
        return responseFromServer(HttpStatus.OK,request,"Review posted successfully");
    }

    @Transactional
    public ResponseEntity<Object> editReview(long reviewID, EditAddReview editReview, HttpServletRequest request, String token) {
        Optional<User> user = userRepository.findById(getUserIDFromToken(token));
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        Optional<Review> review = reviewRepository.findById(reviewID);
        if(review.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Review of id " + reviewID + " was not found");
        }

        if(review.get().getReviewUser().getUserID() != user.get().getUserID()){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Score score = review.get().getScore();
        float editAvgFPS = score.getAvgFPS();
        int editMusicRating = score.getMusicRating();
        int editGraphicsRating = score.getGraphicsRating();
        int editGameplayRating = score.getGameplayRating();

        if(!editReview.getContent().isBlank()){
            if(updateField("reviews","content",editReview.getContent(),"reviewID",String.valueOf(reviewID)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"There was an error updating content of the review");
            }
        }
        if(editReview.getAvgFPS() != editAvgFPS || editReview.getAvgFPS() != 0){
            if(updateField("scores","avgFPS",String.valueOf(editReview.getAvgFPS()),"scoreID",String.valueOf(score.getScoreID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"There was an error updating average FPS in the score");
            }
        }
        if(editReview.getMusicRating() != editMusicRating || editReview.getMusicRating() != 0){
            if(updateField("scores","musicRating",String.valueOf(editReview.getMusicRating()),"scoreID",String.valueOf(score.getScoreID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"There was an error updating music rating in the score");
            }
        }
        if(editReview.getGraphicsRating() != editGraphicsRating || editReview.getGraphicsRating() != 0){
            if(updateField("scores","graphicsRating",String.valueOf(editReview.getGraphicsRating()),"scoreID",String.valueOf(score.getScoreID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"There was an error updating graphics rating in the score");
            }
        }
        if(editReview.getGameplayRating() != editGameplayRating || editReview.getGameplayRating() != 0){
            if(updateField("scores","gameplayRating",String.valueOf(editReview.getGameplayRating()),"scoreID",String.valueOf(score.getScoreID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"There was an error updating gameplay rating in the score");
            }
        }
        return responseFromServer(HttpStatus.OK,request,"Review ID: " + reviewID + " updated successfully");
    }
}

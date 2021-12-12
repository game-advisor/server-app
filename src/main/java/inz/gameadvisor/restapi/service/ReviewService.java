package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.reviewOriented.AddReview;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.model.reviewOriented.Score;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;

    public List<Review> getAllReviewsForGame(long gameID){
        if(reviewRepository.findReviewsBygameID(gameID).isEmpty())
            throw new CustomRepsonses.MyNotFoundException("No elements found");
        else
            return reviewRepository.findAll();
    }

    @SneakyThrows
    public void createReviewForGame(long gameID, AddReview addReview,
                             String token){
        long userID = getUserIDFromToken(token);


        Review review = new Review();

        Score score = new Score();

        if(addReview.getContent().isBlank()){
            throw new CustomRepsonses.MyBadRequestException("Empty content");
        }
        score.setAvgFPS(addReview.getAvgFPS());
        score.setMusicRating(addReview.getMusicRating());
        score.setGameplayRating(addReview.getGameplayRating());
        score.setGraphicsRating(addReview.getGraphicsRating());

        scoreRepository.save(score);

        review.setReviewUserID(userID);
        review.setGameID(gameID);
        review.setContent(addReview.getContent());
        review.setReviewScoreID(score.getScoreID());
        long currentDateTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date resultDate = new Date(currentDateTime);
        review.setDateCreated(resultDate);

        reviewRepository.save(review);
    }

    public long getUserIDFromToken(String token) {
        String[] splitString = token.split("\\.");
        String base64EncodedBody = splitString[1];
        Base64 base64Url = new Base64(true);

        String body = new String(base64Url.decode(base64EncodedBody));
        JSONObject tokenBody = new JSONObject(body);
        long userID = Long.parseLong(tokenBody.get("userID").toString());

        return userID;
    }
}

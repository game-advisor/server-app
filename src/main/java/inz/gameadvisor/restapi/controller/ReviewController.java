package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.reviewOriented.AddReview;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.service.ReviewService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/game/{id}/reviews/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Reviews not found")
    })
    public List<Review> getAllReviewsForGame(@PathVariable("id") long gameID) {
        return reviewService.getAllReviewsForGame(gameID);
    }

    @PostMapping("/api/game/{id}/reviews/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Reviews not found")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void createReviewForGame(@PathVariable("id") long gameID,
                             @RequestBody AddReview addReview,
                             @ApiIgnore @RequestHeader("Authorization") String token){
        reviewService.createReviewForGame(gameID, addReview, token);
    }
}

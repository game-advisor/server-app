package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.reviewOriented.AddReview;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.service.ReviewService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/game/{game_id}/review/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Object> getAllReviewsForGame(@PathVariable("game_id") long gameID,
                                                       HttpServletRequest request) {
        return reviewService.getAllReviewsForGame(gameID,request);
    }

    @PostMapping("/api/game/{game_id}/review/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Object> createReviewForGame(@PathVariable("game_id") long gameID,
                             @RequestBody AddReview addReview,
                             HttpServletRequest request,
                             @ApiIgnore @RequestHeader("Authorization") String token){
        return reviewService.createReviewForGame(gameID, addReview, request, token);
    }
}

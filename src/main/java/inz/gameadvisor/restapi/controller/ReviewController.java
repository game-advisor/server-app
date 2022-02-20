package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.reviewOriented.EditAddReview;
import inz.gameadvisor.restapi.service.ReviewService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/game/{game_id}/review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getAllReviewsForGame(@PathVariable("game_id") long gameID,
                                                       HttpServletRequest request) {
        return reviewService.getAllReviewsForGame(gameID,request);
    }

    @PostMapping("/api/game/{game_id}/review/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> addReview(@PathVariable("game_id") long gameID,
                             @RequestBody EditAddReview editAddReview,
                             HttpServletRequest request,
                             @ApiIgnore @RequestHeader("Authorization") String token){
        return reviewService.addReview(gameID, editAddReview, request, token);
    }

    @PutMapping("/api/review/{review_id}/edit")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> editReview(@PathVariable("review_id") long reviewID,
                                             @RequestBody EditAddReview editReview,
                                             HttpServletRequest request,
                                             @ApiIgnore @RequestHeader("Authorization") String token){
        return reviewService.editReview(reviewID,editReview,request,token);
    }
}

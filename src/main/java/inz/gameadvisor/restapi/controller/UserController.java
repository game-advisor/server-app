package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.model.userOriented.LoginCredentials;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import inz.gameadvisor.restapi.service.FileStorageService;
import inz.gameadvisor.restapi.service.ReviewService;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController extends CustomFunctions {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    @PutMapping("/api/user/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Method has not been used")
    })
    public ResponseEntity<Object> updateUserInfo(@RequestBody UpdateUser updateUser,
                                                 HttpServletRequest request,
                                                 @ApiIgnore @RequestHeader("Authorization") String token) {
        return userService.editUserInfo(updateUser, request, token);
    }

    @GetMapping("/api/user/{user_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Object> getUserInfo(@PathVariable("user_id") long id,
                                              HttpServletRequest request,
                                              @ApiIgnore @RequestHeader("Authorization") String token){
        return userService.getUserInfo(id,request ,token);
    }

    @PostMapping("/api/user/login")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ResponseStatus(code = HttpStatus.OK)
    public void login(@RequestBody LoginCredentials credentials){
    }

    @PostMapping("/api/user/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "422", description = "Body payload is wrong")
    })
    public ResponseEntity<Object> register(@RequestBody RegisterCredentials registerCredentials,
                                           HttpServletRequest request){
        return userService.register(registerCredentials, request);
    }

    @DeleteMapping("/api/user/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401",description = "Unauthorized")
    })
    public ResponseEntity<Object> deleteUser(@ApiIgnore @RequestHeader("Authorization") String token, HttpServletRequest request){
        return userService.deleteUser(token,request);
    }

    @GetMapping("/api/user/{user_id}/avatar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Object> getUserAvatar(@PathVariable("user_id") long id, HttpServletRequest request){

        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            String fileName = user.get().getAvatarPath();
            Resource resource = fileStorageService.loadFileAsResource(fileName);

            if(resource == null){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"Image not found on server");
            }

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Could not determine file type.");
            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        else{
            return responseFromServer(HttpStatus.NOT_FOUND,request,"User was not found");
        }
    }

    @GetMapping("/api/user/favTags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No tags found")
    })
    public ResponseEntity<Object> getUserLikedTags(@ApiIgnore @RequestHeader("Authorization") String token,
                                                   HttpServletRequest request){
        return userService.getUserLikedTags(token, request);
    }

    @PostMapping("/api/user/favTags/{tag_id}/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No tag found"),
            @ApiResponse(responseCode = "409", description = "Tag already in favorites")
    })
    public ResponseEntity<Object> addTagToFavorites(@PathVariable("tag_id") long tagID,
                                                     @ApiIgnore @RequestHeader("Authorization") String token,
                                                     HttpServletRequest request){
        return userService.addTagToFavorites(tagID,token,request);
    }

    @DeleteMapping("/api/user/favTags/{tag_id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No tag found")
    })
    public ResponseEntity<Object> removeTagFromFavorites(@PathVariable("tag_id") long tagID,
                                                          HttpServletRequest request){
        return userService.removeTagFromFavorites(tagID,request);
    }

    @GetMapping("/api/user/favGames")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No games found")
    })
    public ResponseEntity<Object> getUserLikedGames(@ApiIgnore @RequestHeader("Authorization") String token,
                                                    HttpServletRequest request){
        return userService.getUserLikedGames(token, request);
    }

    @PostMapping("/api/user/favGames/{game_id}/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No game found"),
            @ApiResponse(responseCode = "409", description = "Game already in favorites")
    })
    public ResponseEntity<Object> addGameToFavorites(@PathVariable("game_id") long gameID,
                                                     @ApiIgnore @RequestHeader("Authorization") String token,
                                                     HttpServletRequest request){
        return userService.addGameToFavorites(gameID,token,request);
    }

    @DeleteMapping("/api/user/favGames/{game_id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No game found")
    })
    public ResponseEntity<Object> removeGameFromFavorites(@PathVariable("game_id") long gameID,
                                                          HttpServletRequest request){
        return userService.removeGameFromFavorites(gameID,request);
    }

    @GetMapping("/api/user/reviews/get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No reviews found")
    })
    public ResponseEntity<Object> getUserReviews(@ApiIgnore @RequestHeader("Authorization") String token,
                                                 HttpServletRequest request){
        return userService.getUserReviews(token, request);
    }

}
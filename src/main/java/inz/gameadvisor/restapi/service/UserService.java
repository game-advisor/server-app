package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.gameOriented.Tag;
import inz.gameadvisor.restapi.model.reviewOriented.Review;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.GameRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.TagRepository;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService extends CustomFunctions {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public ResponseEntity<Object> editUserInfo(UpdateUser updateUser, HttpServletRequest request, String token) {
        long userID = getUserIDFromToken(token);
        String password = updateUser.getPassword();
        String username = updateUser.getUsername();
        int passwordUpdate = 0, usernameUpdate = 0;

        if(Objects.isNull(password) || Objects.isNull(username)) {
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"Bad request");
        }

        if (!password.isBlank()) {
            password = passwordEncoder.encode(password);
            passwordUpdate = updateField("users","password",password,"userID",String.valueOf(userID));
        }
        if (!username.isBlank()) {
            usernameUpdate = updateField("users","username",username,"userID",String.valueOf(userID));
        }
        if(usernameUpdate == 0 && passwordUpdate == 0){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Records have not been updated");
        }
        else if (usernameUpdate > 0 && passwordUpdate > 0){
            return responseFromServer(HttpStatus.OK,request,"Username and password updated");
        }
        else if(passwordUpdate > 0){
            return responseFromServer(HttpStatus.OK,request,"Password updated");
        }
        else if(usernameUpdate > 0){
            return responseFromServer(HttpStatus.OK,request,"Username updated");
        }
        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Method has not been applied");
    }

    public ResponseEntity<Object> register(RegisterCredentials registerCredentials,
                                           HttpServletRequest request) {
        User user = new User();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@" + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        String username = registerCredentials.getUsername();
        String email = registerCredentials.getEmail();
        String password = registerCredentials.getPassword();
        if(Objects.isNull(email) || Objects.isNull(username) || Objects.isNull(password))
        {
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"Bad request");
        }
        if (username.length() > 64 || email.length() > 255 || password.length() > 32) {
            return responseFromServer(HttpStatus.UNPROCESSABLE_ENTITY,request,"Data too long");
        } else {
            if (!checkEmailValidity(email, regex)) {
                return responseFromServer(HttpStatus.BAD_REQUEST,request,"Email is not in valid format");
            } else {
                Optional<User> userOptional = userRepository.findByUsernameOrEmail(username,email);
                if (userOptional.isPresent()) {
                    return responseFromServer(HttpStatus.CONFLICT,request,"There is a user with such username/email");
                } else {
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setAvatarPath("defaultAvatar128x128.png");
                    user.setEnabled(true);
                    user.setRoles("ROLE_USER");
                    userRepository.save(user);
                    return responseFromServer(HttpStatus.OK,request,"User has been registered");
                }
            }
        }
    }

    public ResponseEntity<Object> getUserInfo(long id, HttpServletRequest request, String token){
        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
        JSONObject userJ = new JSONObject(jsonOrderedMap);

        Optional<User> userU = userRepository.findById(id);
        if(userU.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No such user found");
        }

        if(getUserIDFromToken(token) == id){
            if(isUserAnAdmin(getUserIDFromToken(token))) {
                userJ.put("userID",userU.get().getUserID());
                userJ.put("username", userU.get().getUsername());
                userJ.put("enabled", userU.get().isEnabled());
                userJ.put("email", userU.get().getEmail());
                userJ.put("avatarPath", userU.get().getAvatarPath());
                userJ.put("roles", userU.get().getRoles());
            }
            else {
                userJ.put("userID",userU.get().getUserID());
                userJ.put("username", userU.get().getUsername());
                userJ.put("email", userU.get().getEmail());
                userJ.put("avatarPath", userU.get().getAvatarPath());
            }
            return new ResponseEntity<>(userJ.toMap(), HttpStatus.OK);
        }
        else{
            if(isUserAnAdmin(getUserIDFromToken(token))) {
                userJ.put("userID",userU.get().getUserID());
                userJ.put("username", userU.get().getUsername());
                userJ.put("enabled", userU.get().isEnabled());
                userJ.put("email", userU.get().getEmail());
                userJ.put("avatarPath", userU.get().getAvatarPath());
                userJ.put("roles", userU.get().getRoles());
            }
            else{
                userJ.put("username", userU.get().getUsername());
                userJ.put("avatarPath", userU.get().getAvatarPath());
            }
            return new ResponseEntity<>(userJ.toMap(), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> getUserLikedGames(String token, HttpServletRequest request){
        List<Game> favGames = gameRepository.findByLike_userID(getUserIDFromToken(token));
        if(favGames.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"User has no favorite games");
        }
        return new ResponseEntity<>(favGames.toArray(),HttpStatus.OK);
    }

    public ResponseEntity<Object> getUserLikedTags(String token, HttpServletRequest request){
        List<Tag> favTags = tagRepository.findByLikeTags_userID(getUserIDFromToken(token));
        if(favTags.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"User has no favorite games");
        }
        return new ResponseEntity<>(favTags.toArray(),HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> addGameToFavorites(long gameID, String token, HttpServletRequest request){
        Optional<Game> game = gameRepository.findById(gameID);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        List<Game> favGames = gameRepository.findByLike_userID(getUserIDFromToken(token));
        for (Game favGame : favGames) {
            if(favGame.getGameID() == gameID){
                return responseFromServer(HttpStatus.CONFLICT,request,"Game is already in your favorites");
            }
        }
        Query query = em.createNativeQuery("INSERT INTO favgames (userID,gameID) VALUES (?,?)")
                .setParameter(1, getUserIDFromToken(token))
                .setParameter(2, gameID);
        query.executeUpdate();
        return responseFromServer(HttpStatus.OK,request,"Game added to favorites");
    }

    @Transactional
    public ResponseEntity<Object> removeGameFromFavorites(long gameID, HttpServletRequest request){
        Optional<Game> game = gameRepository.findById(gameID);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        Query query = em.createNativeQuery("DELETE FROM favgames WHERE gameID = ?")
                .setParameter(1, gameID);
        query.executeUpdate();
        return responseFromServer(HttpStatus.OK,request,"Game deleted from favorites");
    }

    @Transactional
    public ResponseEntity<Object> addTagToFavorites(long tagID, String token, HttpServletRequest request){
        Optional<Tag> tag = tagRepository.findById(tagID);
        if(tag.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        List<Tag> favTags = tagRepository.findByLikeTags_userID(getUserIDFromToken(token));
        for (Tag favTag : favTags) {
            if (favTag.getTagID() == tagID) {
                return responseFromServer(HttpStatus.CONFLICT, request, "This tag is already in your favorites");
            }
        }
        Query query = em.createNativeQuery("INSERT INTO favtags (tagID,userID) VALUES (?,?)")
                .setParameter(1, tagID)
                .setParameter(2, getUserIDFromToken(token));
        query.executeUpdate();
        return responseFromServer(HttpStatus.OK,request,"Tag added to favorites");
    }

    @Transactional
    public ResponseEntity<Object> removeTagFromFavorites(long tagID, HttpServletRequest request){
        Optional<Tag> tag = tagRepository.findById(tagID);
        if(tag.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Tag not found");
        }
        Query query = em.createNativeQuery("DELETE FROM favtags WHERE tagID = ?")
                .setParameter(1, tagID);
        query.executeUpdate();
        return responseFromServer(HttpStatus.OK,request,"Tag deleted from favorites");
    }

    public ResponseEntity<Object> deleteUser(String token, HttpServletRequest request) {
        Optional<User> user = userRepository.findById(getUserIDFromToken(token));
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"User was not found");
        }
        try{
            userRepository.deleteById(getUserIDFromToken(token));
        }
        catch (Exception e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"Account deleted successfully");
    }

    public ResponseEntity<Object> getUserReviews(String token, HttpServletRequest request){
        Optional<User> user = userRepository.findById(getUserIDFromToken(token));
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        List<Review> reviewList = reviewRepository.findByReviewUser(user.get());
        if(reviewList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No reviews found for this user");
        }
        return new ResponseEntity<>(reviewList,HttpStatus.OK);
    }

    public ResponseEntity<Object> getUserReviewsByID(long userID, HttpServletRequest request) {
        Optional<User> user = userRepository.findById(userID);
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        List<Review> reviewList = reviewRepository.findByReviewUser(user.get());
        if(reviewList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No reviews found for this user");
        }
        return new ResponseEntity<>(reviewList,HttpStatus.OK);
    }

    public ResponseEntity<Object> checkTokenExpiryTime(String token){
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        Timestamp tokenExpiryDate = new Timestamp(getTokenExpiryTimeFromToken(token));
        long timeLeft = tokenExpiryDate.getTime() - currentDate.getTime();
        long diffSeconds = timeLeft / 1000 % 60;
        long diffMinutes = timeLeft / (60 * 1000) % 60;
        long diffHours = timeLeft / (60 * 60 * 1000) % 24;
        long diffDays = timeLeft / (24 * 60 * 60 * 1000);
        String message = "Token valid for: " + diffDays + " days, " + diffHours + " hours, " + diffMinutes + " minutes, " + diffSeconds + " seconds.";
        JSONObject json = new JSONObject();
        json.put("Message",message);
        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
}



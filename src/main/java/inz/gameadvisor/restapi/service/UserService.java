package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService extends CustomFunctions {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomFunctions customFunctions = new CustomFunctions();
    private String path;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public ResponseEntity<Object> editUserInfo(UpdateUser updateUser, String token) throws CustomRepsonses.MyNotFoundException {
        long userID = getUserIDFromToken(token);
        String password = updateUser.getPassword();
        String username = updateUser.getUsername();
        int passwordUpdate = 0, usernameUpdate = 0;

        if(Objects.isNull(password) || Objects.isNull(username)) {
            return responseFromServer(HttpStatus.BAD_REQUEST,"/api/user/edit","Bad request");
        }

        if (!password.isBlank()) {
            password = passwordEncoder.encode(password);
            passwordUpdate = updateField("users","password",password,"userID",String.valueOf(userID));
        }
        if (!username.isBlank()) {
            usernameUpdate = updateField("users","username",username,"userID",String.valueOf(userID));
        }
        if(usernameUpdate == 0 && passwordUpdate == 0){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,"/api/user/edit","Records have not been updated");
        }
        else if (usernameUpdate > 0 && passwordUpdate > 0){
            return responseFromServer(HttpStatus.OK,"/api/user/edit","Username and password updated");
        }
        else if(passwordUpdate > 0){
            return responseFromServer(HttpStatus.OK,"/api/user/edit","Password updated");
        }
        else if(usernameUpdate > 0){
            return responseFromServer(HttpStatus.OK,"/api/user/edit","Username updated");
        }
        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,"/api/user/edit","Method has not been applied");
    }

    public ResponseEntity<Object> register(RegisterCredentials registerCredentials) {
        User user = new User();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
                + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        String username = registerCredentials.getUsername();
        String email = registerCredentials.getEmail();
        if(Objects.isNull(email) || Objects.isNull(username) || Objects.isNull(registerCredentials.getPassword()))
        {
            return responseFromServer(HttpStatus.BAD_REQUEST,"/api/user/register","Bad request");
        }
        if (username.length() > 64 || email.length() > 255 || registerCredentials.getPassword().length() > 32) {
            return responseFromServer(HttpStatus.UNPROCESSABLE_ENTITY,"/api/user/register","Data too long");
        } else {
            if (!checkEmailValidity(email, regex)) {
                return responseFromServer(HttpStatus.BAD_REQUEST,"/api/user/register","Email is not in valid format");
            } else {
                Query emailQuery = em.createNativeQuery("SELECT email,username FROM users WHERE email = ? OR username = ?")
                        .setParameter(1, email)
                        .setParameter(2, username);
                List results1 = emailQuery.getResultList();
                if (!results1.isEmpty()) {
                    return responseFromServer(HttpStatus.CONFLICT,"/api/user/register","There is a user with such username/email");
                } else {
                    user.setEmail(registerCredentials.getEmail());
                    user.setUsername(registerCredentials.getUsername());
                    user.setPassword(passwordEncoder.encode(registerCredentials.getPassword()));
                    user.setAvatarPath("defaultAvatar128x128.png");
                    user.setEnabled(true);
                    user.setRoles("ROLE_USER");
                    userRepository.save(user);
                    return responseFromServer(HttpStatus.OK,"/api/user/register","User has been registered");
                }
            }
        }
    }

    public ResponseEntity<Object> getUserInfo(long id, String token){
        long userIDToken = getUserIDFromToken(token);

        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();

        JSONObject userJ = new JSONObject(jsonOrderedMap);

        User userU = userRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("User of ID: " + id + " not found."));

        if(userIDToken == id){
            if(isUserAnAdmin(userIDToken)) {
                userJ.put("userID",userU.getUserID());
                userJ.put("username", userU.getUsername());
                userJ.put("password", userU.getPassword());
                userJ.put("enabled", userU.isEnabled());
                userJ.put("email", userU.getEmail());
                userJ.put("avatarPath", userU.getAvatarPath());
                userJ.put("roles", userU.getRoles());
            }
            else {
                userJ.put("userID",userU.getUserID());
                userJ.put("username", userU.getUsername());
                userJ.put("email", userU.getEmail());
                userJ.put("avatarPath", userU.getAvatarPath());
            }
            return new ResponseEntity<>(userJ.toMap(), HttpStatus.OK);
        }
        else{
            if(isUserAnAdmin(userIDToken)) {
                userJ.put("userID",userU.getUserID());
                userJ.put("username", userU.getUsername());
                userJ.put("password", userU.getPassword());
                userJ.put("enabled", userU.isEnabled());
                userJ.put("email", userU.getEmail());
                userJ.put("avatarPath", userU.getAvatarPath());
                userJ.put("roles", userU.getRoles());
            }
            else{
                userJ.put("username", userU.getUsername());
                userJ.put("avatarPath", userU.getAvatarPath());
            }
            return new ResponseEntity<>(userJ.toMap(), HttpStatus.OK);
        }
    }

    public static boolean checkEmailValidity(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public JSONObject getBodyFromToken(String token){
        String[] splitString = token.split("\\.");
        String base64EncodedBody = splitString[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));

        return new JSONObject(body);
    }

    public long getUserIDFromToken(String token){
        JSONObject tokenBody = getBodyFromToken(token);

        return Long.parseLong(tokenBody.get("userID").toString());
    }

    public boolean isUserAnAdmin(long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals("ROLE_ADMIN");
    }
}



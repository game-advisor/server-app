package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService extends CustomFunctions {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                Query emailQuery = em.createNativeQuery("SELECT email,username FROM users WHERE email = ? OR username = ?")
                        .setParameter(1, email)
                        .setParameter(2, username);
                List<?> results1 = emailQuery.getResultList();
                if (!results1.isEmpty()) {
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

    //List the GPUs, CPUs, RAMs
}



package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public User getUserInfo(long id, String token) throws MyUserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new MyUserNotFoundException("No such user"));
    }

    @Transactional
    public void updateUserInfo(UpdateUser updateUser, String token) throws MyUserNotFoundException {
        long userID = getUserIDFromToken(token);
        String password = updateUser.getPassword();
        String username = updateUser.getUsername();

        if(!password.equals("")){
            password = passwordEncoder.encode(password);
            Query query1 = em.createNativeQuery("UPDATE users SET password = ? WHERE userID = ?")
                    .setParameter(1, password)
                    .setParameter(2, userID);
            query1.executeUpdate();
        }
        if(!username.equals("")){
            Query query1 = em.createNativeQuery("UPDATE users SET username = ? WHERE userID = ?")
                    .setParameter(1, username)
                    .setParameter(2, userID);
            query1.executeUpdate();
        }
    }

    public void register(RegisterCredentials registerCredentials){
        User user = new User();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String username = registerCredentials.getUsername();
        String email = registerCredentials.getEmail();
        if(username.length() > 64 || email.length() > 255 || registerCredentials.getPassword().length() > 32){
            throw new MyDataConflict("Something in your body payload is wrong");
        }
        else{
            if(!checkEmailValidity(email, regex)){
                throw new MyDataConflict("Something in your body payload is wrong");
            }
            else{
                Query emailQuery = em.createNativeQuery("SELECT email FROM users WHERE email = ?")
                        .setParameter(1, email);

                Query usernameQuery = em.createNativeQuery("SELECT username FROM users WHERE username = ?")
                        .setParameter(1, username);

                List results = usernameQuery.getResultList();
                List results1 = emailQuery.getResultList();
                if(!results.isEmpty() || !results1.isEmpty()){
                    throw new MyDataConflict("Data duplicated");
                }
                else{
                    user.setEmail(registerCredentials.getEmail());
                    user.setUsername(registerCredentials.getUsername());
                    user.setPassword(passwordEncoder.encode(registerCredentials.getPassword()));
                    user.setAvatarPath("img/defaultAvatar64x64.png");
                    user.setEnabled(true);
                    user.setRoles("ROLE_USER");
                    userRepository.save(user);
                }
            }
        }
    }

    public static boolean checkEmailValidity(String emailAddress, String regexPattern){
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public long getUserIDFromToken(String token){
        String[] splitString = token.split("\\.");
        String base64EncodedBody = splitString[1];
        Base64 base64Url = new Base64(true);

        String body = new String(base64Url.decode(base64EncodedBody));
        JSONObject tokenBody = new JSONObject(body);
        long userID = Long.parseLong(tokenBody.get("userID").toString());

        return userID;
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    public static class MyDataConflict extends DataIntegrityViolationException{
        public MyDataConflict(String message){
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public static class MyForbiddenAccess extends IllegalAccessException{
        public MyForbiddenAccess(String message){
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class MyUserNotFoundException extends NoSuchElementException {
        public MyUserNotFoundException(String message)
        {
            super(message);
        }
    }
}



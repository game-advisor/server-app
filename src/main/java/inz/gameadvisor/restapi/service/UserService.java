package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.model.RegisterCredentials;
import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import javassist.bytecode.DuplicateMemberException;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.*;
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

    public void updateUserInfo(User user, String token) throws MyUserNotFoundException {
        //Retrieve userID from passed User object in Request Body
        long userID = user.getUserID();

        String username = "";

        //Native query to search if user exists
        Query query = em.createNativeQuery("SELECT username FROM users WHERE userID = ?")
                .setParameter(1,userID);

        //Try catch to find out if users exists, throws custom
        //NotFound exception on failure and returns 404 code to the browser
        try
        {
             username = query.getSingleResult().toString();
        }
        catch (NoResultException e) {
            throw new MyUserNotFoundException("User not found");
        }

//        //If try catch passes, select all user info from the table
//        Query query1 = em.createNativeQuery("SELECT * FROM users WHERE userID = ?", User.class)
//                .setParameter(1,userID);
//
//        User user1 = (User) query1.getSingleResult();
//
//        User updatedUser = new User();
//
//        System.out.println(user1.getUserID() + " " + user1.getUsername()  + " " +  user1.getPassword()  + " " +  user1.getEnabled()  + " " +  user1.getEmail()  + " " +  user1.getAvatarPath()  + " " +  user1.getAuthorityID());
//
//        if(user.getUsername() != "")
//            updatedUser.setUsername(user.getUsername());
//
//
//        String usrname = user1.getUsername();
//        String password = user1.getPassword();
//        String enabled = user1.getEnabled();
//        String email = user1.getEmail();
//        String avatarPath = user1.getAvatarPath();
//        long authorityID = user1.getAuthorityID();
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

    public static boolean checkEmailValidity(String emailAddr, String regexPattern){
        return Pattern.compile(regexPattern)
                .matcher(emailAddr)
                .matches();
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    public class MyDataConflict extends DataIntegrityViolationException{
        public MyDataConflict(String message){
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



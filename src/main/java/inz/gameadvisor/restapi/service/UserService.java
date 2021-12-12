package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void editUserInfo(UpdateUser updateUser, String token) throws CustomRepsonses.MyNotFoundException {
        long userID = getUserIDFromToken(token);
        String password = updateUser.getPassword();
        String username = updateUser.getUsername();

        if(Objects.isNull(password) || Objects.isNull(username)) {
            throw new CustomRepsonses.MyBadRequestException("Yoooooo");
        }

        if (!password.isBlank()) {
            password = passwordEncoder.encode(password);
            Query query1 = em.createNativeQuery("UPDATE users SET password = ? WHERE userID = ?")
                    .setParameter(1, password)
                    .setParameter(2, userID);
            query1.executeUpdate();
        }
        if (!username.isBlank()) {
            Query query1 = em.createNativeQuery("UPDATE users SET username = ? WHERE userID = ?")
                    .setParameter(1, username)
                    .setParameter(2, userID);
            query1.executeUpdate();
        }
    }

    public void register(RegisterCredentials registerCredentials) {
        User user = new User();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
                + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        String username = registerCredentials.getUsername();
        String email = registerCredentials.getEmail();
        if(Objects.isNull(email) || Objects.isNull(username))
        {
            throw new CustomRepsonses.MyBadRequestException("Yoooooo");
        }
        if (username.length() > 64 || email.length() > 255 || registerCredentials.getPassword().length() > 32) {
            throw new CustomRepsonses.MyDataConflict("Data too long");
        } else {
            if (!checkEmailValidity(email, regex)) {
                throw new CustomRepsonses.MyDataConflict("Mail did not fit regex");
            } else {
                Query emailQuery = em.createNativeQuery("SELECT email,username FROM users WHERE email = ? OR username = ?")
                        .setParameter(1, email)
                        .setParameter(2, username);
                List results1 = emailQuery.getResultList();
                if (!results1.isEmpty()) {
                    throw new CustomRepsonses.MyDataConflict("Data duplicated");
                } else {
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

    public static boolean checkEmailValidity(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
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



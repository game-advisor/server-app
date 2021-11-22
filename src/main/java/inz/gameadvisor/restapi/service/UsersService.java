package inz.gameadvisor.restapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inz.gameadvisor.restapi.model.Users;
import inz.gameadvisor.restapi.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public Users getUsersInfo(long id, String token, String secret) throws UsersNotAllowed {

        String jwtToken = token;
        String[] split_string = jwtToken.split("\\.");
        String body = split_string[1];
        Base64 base64URL = new Base64(true);
        String decodedBody = new String(base64URL.decode(body));

        JSONObject jsonBody = new JSONObject(decodedBody);

        String userID = jsonBody.get("userID").toString();

        Integer UID = Integer.parseInt(userID);

        if(id == UID)
            return usersRepository.findById(id).orElseThrow(() -> new UsersNotFoundException("No such user"));
        else
            throw new UsersNotAllowed("Not allowed");
    }


    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public class UsersNotFoundException extends NoSuchElementException {
        public UsersNotFoundException(String message)
        {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public class UsersNotAllowed extends IllegalAccessException {
        public UsersNotAllowed(String message)
        {
            super(message);
        }
    }
}



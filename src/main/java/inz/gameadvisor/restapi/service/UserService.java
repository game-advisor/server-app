package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUsersInfo(long id, String token) throws UserService.UsersNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UsersNotFoundException("No such user"));
    }

    public User updateUserInfo(long id, User user) {
        System.out.println(user.getUsername());
        return null;
//        return userRepository.save(user);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class UsersNotFoundException extends NoSuchElementException {
        public UsersNotFoundException(String message)
        {
            super(message);
        }
    }
}



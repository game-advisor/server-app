package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.UnknownServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User getUserInfo(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No such user"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public class UserNotFoundException extends NoSuchElementException {
        public UserNotFoundException(String message)
        {
            super(message);
        }
    }
}



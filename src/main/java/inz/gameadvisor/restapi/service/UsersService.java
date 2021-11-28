package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.model.Users;
import inz.gameadvisor.restapi.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public Users getUsersInfo(long id) {

        return usersRepository.findById(id).orElseThrow(() -> new UsersNotFoundException("No such user"));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public class UsersNotFoundException extends NoSuchElementException {
        public UsersNotFoundException(String message)
        {
            super(message);
        }
    }
}



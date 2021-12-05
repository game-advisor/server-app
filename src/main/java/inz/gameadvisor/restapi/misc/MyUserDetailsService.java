package inz.gameadvisor.restapi.misc;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import inz.gameadvisor.restapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = null;

        try{
            user = userRepository.findByUsername(username);
        }
        catch(NoSuchElementException e)
        {
            throw new UserService.MyUserNotFoundException("User not found " + username);
        }

        //user.orElseThrow(() -> new UsernameNotFoundException("Not found " + username));

        return user.map(MyUserDetails::new).get();
    }
}

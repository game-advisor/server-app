package inz.gameadvisor.restapi.misc;

import inz.gameadvisor.restapi.model.userOriented.User;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> user = null;

        try{
            user = userRepository.findByEmail(email);
        }
        catch(UsernameNotFoundException e)
        {
            throw new CustomRepsonses.MyNotFoundException("User not found " + email);
        }

        //user.orElseThrow(() -> new UsernameNotFoundException("Not found " + username));

        return user.map(MyUserDetails::new).get();
    }
}

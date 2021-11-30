package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.RegisterCredentials;
import inz.gameadvisor.restapi.model.Authorities;
import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.repository.AuthoritiesRepository;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public User register(@RequestBody RegisterCredentials registerCredentials){
        List<Authorities> authorities = authoritiesRepository.findAll();

        authorities.toString();

        User user = new User();
        user.setEmail(registerCredentials.getEmail());
        user.setUsername(registerCredentials.getUsername());
        user.setPassword(passwordEncoder.encode(registerCredentials.getPassword()));
        user.setAvatarPath("img/defaultAvatar64x64.png");
        user.setEnabled("1");

        return userRepository.save(user);
    }
}

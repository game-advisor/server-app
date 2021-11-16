package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.config.RegisterCredentials;
import inz.gameadvisor.restapi.model.Authorities;
import inz.gameadvisor.restapi.model.Users;
import inz.gameadvisor.restapi.repository.AuthorityRepository;
import inz.gameadvisor.restapi.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final UsersRepository usersRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Users register(@RequestBody RegisterCredentials registerCredentials){
        Authorities authorities = new Authorities();
        authorities.setUsername(registerCredentials.getUsername());
        authorities.setAuthority("ROLE_USER");
        authorityRepository.save(authorities);

        Users user = new Users();
        user.setEmail(registerCredentials.getEmail());
        user.setUsername(registerCredentials.getUsername());
        user.setPassword(passwordEncoder.encode(registerCredentials.getPassword()));
        user.setAvatarPath("img/defaultAvatar64x64.png");
        user.setEnabled("1");

        return usersRepository.save(user);
    };
}

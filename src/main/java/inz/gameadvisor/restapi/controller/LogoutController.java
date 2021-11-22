package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.Users;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class LogoutController {

    @PostMapping("/logout")
    public void logout(){
//        SecurityContextHolder.getContext().getAuthentication().getCredentials();

    }
}

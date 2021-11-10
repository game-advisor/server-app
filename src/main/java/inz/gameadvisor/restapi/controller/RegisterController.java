package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.config.RegisterCredentials;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @PostMapping("/register")
    public void register(@RequestBody RegisterCredentials registerCredentials){

    };
}

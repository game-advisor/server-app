package inz.gameadvisor.restapi.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class LogoutController {

    @PostMapping("/user/logout")
    public void logout(){
    }
}

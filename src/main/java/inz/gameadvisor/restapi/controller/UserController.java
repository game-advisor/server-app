package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{id}")
    public User getUserInfo(@PathVariable long id) {return userService.getUserInfo(id);}
}

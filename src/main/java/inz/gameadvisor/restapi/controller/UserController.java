package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping("/users")
    @ApiOperation(value = "",authorizations = {@Authorization(value = "jwtToken")})
    public List<User> getUsers() {return userService.getUsers();}

    @GetMapping("/user/{id}")
    public User getUserInfo(@PathVariable long id) {return userService.getUserInfo(id);}
}

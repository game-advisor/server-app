package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.Users;
import inz.gameadvisor.restapi.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    @Value("${jwt.secret")
    private String secret;


    @GetMapping("/users/{id}")
    public Users getUsersInfo(@PathVariable long id,
                              @ApiIgnore @RequestHeader("Authorization") String token,
                              @ApiIgnore String secret) throws UsersService.UsersNotAllowed {
        return usersService.getUsersInfo(id,token,secret);
    }
}

package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${jwt.secret")
    private String secret;


    @GetMapping("/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUsersInfo(@PathVariable long id,
                             @ApiIgnore @RequestHeader("Authorization") String token) throws UserService.UsersNotFoundException {
        return userService.getUsersInfo(id,token);
    }

    @PutMapping("/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public User updateUserInfo(@PathVariable long id,
                               @ApiIgnore @AuthenticationPrincipal User user) throws UserService.UsersNotFoundException {
        return userService.updateUserInfo(id,user);
    }

}

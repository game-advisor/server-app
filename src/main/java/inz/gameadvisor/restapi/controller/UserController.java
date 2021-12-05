package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.LoginCredentials;
import inz.gameadvisor.restapi.model.RegisterCredentials;
import inz.gameadvisor.restapi.model.User;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${jwt.secret")
    private String secret;



    @GetMapping("/api/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUserInfo(@PathVariable long id,
                             @ApiIgnore @RequestHeader("Authorization") String token) throws UserService.MyUserNotFoundException {
        return userService.getUserInfo(id,token);
    }

    @PutMapping("/api/user/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void updateUserInfo(@RequestBody User user,
                               @ApiIgnore @RequestHeader("Authorization") String token) throws UserService.MyUserNotFoundException {
        userService.updateUserInfo(user, token);
    }

    @PostMapping("/api/user/login")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void login(@RequestBody LoginCredentials credentials){
    }

    @PostMapping("/api/user/register")
    @ApiResponse(responseCode = "409", description = "Conflict")
    public void register(@RequestBody RegisterCredentials registerCredentials){
        userService.register(registerCredentials);
    }

}

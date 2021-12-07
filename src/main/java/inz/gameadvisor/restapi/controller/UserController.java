package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.LoginCredentials;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${jwt.secret")
    private String secret;

    @PutMapping("/api/user/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateUserInfo(@RequestBody UpdateUser updateUser,
                               @ApiIgnore @RequestHeader("Authorization") String token) throws CustomRepsonses.MyNotFoundException {
        userService.updateUserInfo(updateUser, token);
    }

    @PostMapping("/api/user/login")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void login(@RequestBody LoginCredentials credentials){
    }

    @PostMapping("/api/user/register")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void register(@RequestBody RegisterCredentials registerCredentials){
        userService.register(registerCredentials);
    }

}

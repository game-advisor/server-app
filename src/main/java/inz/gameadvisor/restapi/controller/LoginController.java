package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.LoginCredentials;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class LoginController {

    @PostMapping("/login")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void login(@RequestBody LoginCredentials credentials){

    }
}

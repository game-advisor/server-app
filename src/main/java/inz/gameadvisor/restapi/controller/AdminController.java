package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.service.AdminService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/api/admin/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUserInfo(@PathVariable("id") long id,
                            @ApiIgnore @RequestHeader("Authorization") String token) throws CustomRepsonses.MyNotFoundException {
        return adminService.getUserInfo(id,token);
    }

    @GetMapping("/api/admin/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<User>> getAllUsersList(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "userID") String sortBy,
            @ApiIgnore @RequestHeader("Authorization") String token){
        List<User> list = adminService.getAllUsersList(pageNumber,pageSize,sortBy,token);
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/api/admin/devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No devices found")
    })
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<Devices>> getAllDevicesList(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "deviceID") String sortBy,
            @ApiIgnore @RequestHeader("Authorization") String token){
        List<Devices> list = adminService.getAllDevicesList(pageNumber,pageSize,sortBy,token);
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }
}

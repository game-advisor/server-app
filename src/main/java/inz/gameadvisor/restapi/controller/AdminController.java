package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.service.AdminService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<Object> getUserInfo(@PathVariable("id") long id,
                                              HttpServletRequest request,
                                              @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.getUserInfo(id,request,token);
    }

    @GetMapping("/api/admin/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<Object> getAllUsersInfo(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(defaultValue = "15") Integer pageSize,
                                                  @RequestParam(defaultValue = "userID") String sortBy,
                                                  @ApiIgnore @RequestHeader("Authorization") String token,
                                                  HttpServletRequest request){
        return adminService.getAllUsersInfo(pageNumber,pageSize,sortBy,token,request);
    }

    @GetMapping("/api/admin/devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No devices found")
    })
    public ResponseEntity<Object> getAllDevicesList(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "deviceID") String sortBy,
                                                    @ApiIgnore @RequestHeader("Authorization") String token,
                                                    HttpServletRequest request){
        return adminService.getAllDevicesList(pageNumber,pageSize,sortBy,token, request);
    }

    @PostMapping("/api/admin/cpu/seed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No devices found")
    })
    public ResponseEntity<Object> populateCPU(@ApiIgnore @RequestHeader("Authorization") String token,
                                              HttpServletRequest request){
        return adminService.populateCPU(token,request);
    }

    @PostMapping("/api/admin/gpu/seed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No devices found")
    })
    public ResponseEntity<Object> populateGPU(@ApiIgnore @RequestHeader("Authorization") String token,
                                              HttpServletRequest request){
        return adminService.populateGPU(token,request);
    }
}

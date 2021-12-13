package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
import inz.gameadvisor.restapi.model.reviewOriented.Score;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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
        return new ResponseEntity<List<User>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/api/admin/companies")


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
        return new ResponseEntity<List<Devices>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/api/admin/cpu/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No manufacturer found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addCPU(@RequestBody EditAddCPU cpuAdded,
                       @ApiIgnore @RequestHeader("Authorization") String token){
        adminService.addCPU(cpuAdded,token);
    }

    @PutMapping("/api/admin/cpu/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such cpu found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void editCPU(@PathVariable("id") long id,
                       @RequestBody EditAddCPU editCPU,
                       @ApiIgnore @RequestHeader("Authorization") String token){
        adminService.editCPU(id, editCPU, token);
    }

    @PostMapping("/api/admin/gpu/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No manufacturer found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addGPU(@RequestBody EditAddGPU gpuAdded,
                       @ApiIgnore @RequestHeader("Authorization") String token){
        adminService.addGPU(gpuAdded, token);
    }

    @PutMapping("/api/admin/gpu/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such cpu found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void editGPU(@PathVariable("id") long id,
                        @RequestBody EditAddGPU editGPU,
                        @ApiIgnore @RequestHeader("Authorization") String token){
        adminService.editGPU(id, editGPU, token);
    }

}

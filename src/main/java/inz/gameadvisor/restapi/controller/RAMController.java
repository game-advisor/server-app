package inz.gameadvisor.restapi.controller;


import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddRAM;
import inz.gameadvisor.restapi.service.AdminService;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class RAMController {

    private final AdminService adminService;

    @PostMapping("/api/admin/ram/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data conflict"),
            @ApiResponse(responseCode = "500", description = "Internal error when adding record")
    })
    public ResponseEntity<Object> addRAM(@RequestBody EditAddRAM addRAM,
                                         HttpServletRequest request,
                                         @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addRAM(addRAM,request, token);
    }


    @PutMapping("/api/admin/ram/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data conflict"),
            @ApiResponse(responseCode = "500", description = "Internal error when updating record")
    })
    public ResponseEntity<Object> editRAM(@PathVariable("id") long id,
                        @RequestBody EditAddRAM editRAM,
                        HttpServletRequest request,
                        @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editRAM(id, editRAM, request, token);
    }

    @DeleteMapping("/api/admin/ram/{id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteRAM(@PathVariable("id") long id,
                                           HttpServletRequest request,
                                           @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteRAM(id, request, token);
    }
}

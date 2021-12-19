package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddOS;
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
public class OSController {

    private final AdminService adminService;

    @PostMapping("/api/admin/os/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No manufacturer found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when adding record")
    })
    public ResponseEntity<Object> addOS(@RequestBody EditAddOS addOS,
                                         HttpServletRequest request,
                                         @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addOS(addOS,request,token);
    }

    @PutMapping("/api/admin/os/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such cpu found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when updating record")
    })
    public ResponseEntity<Object> editOS(@PathVariable("id") long id,
                                          @RequestBody EditAddOS editOS,
                                          HttpServletRequest request,
                                          @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editOS(id, editOS, request, token);
    }

    @DeleteMapping("/api/admin/os/{id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteOS(@PathVariable("id") long id,
                                           HttpServletRequest request,
                                           @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteOS(id, request, token);
    }
}

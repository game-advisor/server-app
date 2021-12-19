package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.service.AdminService;
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
public class CPUController {

    private final AdminService adminService;

    @PostMapping("/api/admin/cpu/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when adding record")
    })
    public ResponseEntity<Object> addCPU(@RequestBody EditAddCPU addCPU,
                                         HttpServletRequest request,
                                         @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addCPU(addCPU,request,token);
    }

    @PutMapping("/api/admin/cpu/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when updating record")
    })
    public ResponseEntity<Object> editCPU(@PathVariable("id") long id,
                        @RequestBody EditAddCPU editCPU,
                        HttpServletRequest request,
                        @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editCPU(id, editCPU, request, token);
    }

    @DeleteMapping("/api/admin/cpu/{id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteCPU(@PathVariable("id") long id,
                                            HttpServletRequest request,
                                            @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteCPU(id, request, token);
    }
}

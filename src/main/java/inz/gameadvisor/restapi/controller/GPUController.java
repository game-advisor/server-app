package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
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
public class GPUController {

    private final AdminService adminService;

    @PostMapping("/api/admin/gpu/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when adding record")
    })
    public ResponseEntity<Object> addGPU(@RequestBody EditAddGPU addGPU,
                       HttpServletRequest request,
                       @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addGPU(addGPU, request, token);
    }

    @PutMapping("/api/admin/gpu/{id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when updating record")
    })
    public ResponseEntity<Object> editGPU(@PathVariable("id") long id,
                                  @RequestBody EditAddGPU editGPU,
                                  HttpServletRequest request,
                                  @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editGPU(id, editGPU, request, token);
    }

    @DeleteMapping("/api/admin/gpu/{id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteGPU(@PathVariable("id") long id,
                                           HttpServletRequest request,
                                           @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteGPU(id, request, token);
    }
}

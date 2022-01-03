package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.model.deviceOriented.EditAddOS;
import inz.gameadvisor.restapi.service.AdminService;
import inz.gameadvisor.restapi.service.DevicesService;
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
    private final DevicesService devicesService;

    @PostMapping("/api/admin/os/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when adding record")
    })
    public ResponseEntity<Object> addOS(@RequestBody EditAddOS addOS,
                                         HttpServletRequest request,
                                         @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addOS(addOS,request,token);
    }

    @PutMapping("/api/admin/os/{os_id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when updating record")
    })
    public ResponseEntity<Object> editOS(@PathVariable("os_id") long id,
                                          @RequestBody EditAddOS editOS,
                                          HttpServletRequest request,
                                          @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editOS(id, editOS, request, token);
    }

    @DeleteMapping("/api/admin/os/{os_id}/delete")
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

    @GetMapping("/api/os/{company_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getOSByCompanyName(@PathVariable("company_name") String companyName,
                                                     HttpServletRequest request){
        return devicesService.getOSByCompanyName(companyName,request);
    }
}

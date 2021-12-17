package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
import inz.gameadvisor.restapi.service.AdminService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class CPUController {

    private final AdminService adminService;

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
}

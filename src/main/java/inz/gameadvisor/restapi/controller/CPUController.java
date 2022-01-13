package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddCPU;
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
public class CPUController {

    private final AdminService adminService;
    private final DevicesService devicesService;

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

    @PutMapping("/api/admin/cpu/{cpu_id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when updating record")
    })
    public ResponseEntity<Object> editCPU(@PathVariable("cpu_id") long id,
                        @RequestBody EditAddCPU editCPU,
                        HttpServletRequest request,
                        @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editCPU(id, editCPU, request, token);
    }

    @DeleteMapping("/api/admin/cpu/{cpu_id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteCPU(@PathVariable("cpu_id") long id,
                                            HttpServletRequest request,
                                            @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteCPU(id, request, token);
    }

    @GetMapping("/api/cpu/{cpu_series}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getAllCPUListBySeries(@PathVariable("cpu_series") String series,
                                                        HttpServletRequest request){
        return devicesService.getAllCPUListBySeries(series,request);
    }

    @GetMapping("/api/cpu/{cpu_model_name}/modelInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getCPUInfoByModelName(@PathVariable("cpu_model_name") String model,
                                                        HttpServletRequest request){
        return devicesService.getCPUInfoByModelName(model,request);
    }

    @GetMapping("/api/cpu/series/{company_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getCPUSeriesByCompany(@PathVariable("company_name") String companyName,
                                                        HttpServletRequest request){
        return devicesService.getCPUSeriesByCompany(companyName,request);
    }
}

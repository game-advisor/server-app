package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
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
public class GPUController {

    private final AdminService adminService;
    private final DevicesService devicesService;

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

    @PutMapping("/api/admin/gpu/{gpu_id}/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "500", description = "Internal error when updating record")
    })
    public ResponseEntity<Object> editGPU(@PathVariable("gpu_id") long id,
                                  @RequestBody EditAddGPU editGPU,
                                  HttpServletRequest request,
                                  @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.editGPU(id, editGPU, request, token);
    }

    @DeleteMapping("/api/admin/gpu/{gpu_id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred when deleting record")
    })
    public ResponseEntity<Object> deleteGPU(@PathVariable("gpu_id") long id,
                                           HttpServletRequest request,
                                           @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.deleteGPU(id, request, token);
    }

    @GetMapping("/api/gpu/{gpu_series}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getAllGPUListBySeries(@PathVariable("gpu_series") String series,
                                                        HttpServletRequest request){
        return devicesService.getAllGPUListBySeries(series,request);
    }

    @GetMapping("/api/gpu/{gpu_model_name}/modelInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getGPUInfoByModelName(@PathVariable("gpu_model_name") String model,
                                                        HttpServletRequest request){
        return devicesService.getGPUInfoByModelName(model,request);
    }

    @GetMapping("/api/gpu/series/{company_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> getGPUSeriesByCompany(@PathVariable("company_name") String companyName,
                                                        HttpServletRequest request){
        return devicesService.getGPUSeriesByCompany(companyName,request);
    }
}

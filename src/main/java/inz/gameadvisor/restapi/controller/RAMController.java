package inz.gameadvisor.restapi.controller;


import inz.gameadvisor.restapi.model.deviceOriented.EditAddGPU;
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
public class RAMController {

    private final AdminService adminService;

//    @PostMapping("/api/admin/gpu/add")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Unauthorized"),
//            @ApiResponse(responseCode = "403", description = "Forbidden"),
//            @ApiResponse(responseCode = "404", description = "No manufacturer found"),
//            @ApiResponse(responseCode = "409", description = "Data duplicated")
//    })
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    public void addGPU(@RequestBody EditAddGPU gpuAdded,
//                       @ApiIgnore @RequestHeader("Authorization") String token){
//        adminService.addGPU(gpuAdded, token);
//    }
//
//    @PutMapping("/api/admin/gpu/{id}/edit")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Unauthorized"),
//            @ApiResponse(responseCode = "403", description = "Forbidden"),
//            @ApiResponse(responseCode = "404", description = "No such cpu found"),
//            @ApiResponse(responseCode = "409", description = "Data duplicated")
//    })
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    public void editGPU(@PathVariable("id") long id,
//                        @RequestBody EditAddGPU editGPU,
//                        @ApiIgnore @RequestHeader("Authorization") String token){
//        adminService.editGPU(id, editGPU, token);
//    }
}

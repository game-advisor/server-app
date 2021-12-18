package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.deviceOriented.UpdatedDevices;
import inz.gameadvisor.restapi.service.DevicesService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final DevicesService devicesService;

    @GetMapping("/api/device/user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No device found")
    })
    public ResponseEntity<Object> getDevicesByCurrentUserID(@ApiIgnore @RequestHeader("Authorization") String token,
                                                            HttpServletRequest request){
        return devicesService.getDevicesByCurrentUserID(token, request);
    }

    @PostMapping("/api/device/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<Object> addDevice(@RequestBody UpdatedDevices device,
                                            HttpServletRequest request,
                                            @ApiIgnore @RequestHeader("Authorization") String token){
        return devicesService.addDevice(device,request,token);
    }

    @DeleteMapping("/api/device/{id}/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such device found")
    })
    public ResponseEntity<Object> deleteDevice(@PathVariable("id") long id,
                                               HttpServletRequest request,
                                               @ApiIgnore @RequestHeader("Authorization") String token){
        return devicesService.deleteDevice(id,request,token);
    }

    @PutMapping(value = "/api/device/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such device found"),
            @ApiResponse(responseCode = "409", description = "Data conflict")
    })
    public ResponseEntity<Object> editDevice(@RequestBody UpdatedDevices updatedDevices,
                                     @PathVariable("id") long id,
                                     HttpServletRequest request,
                                     @ApiIgnore @RequestHeader("Authorization") String token){
        return devicesService.editDevice(updatedDevices,request ,id ,token);
    }
}

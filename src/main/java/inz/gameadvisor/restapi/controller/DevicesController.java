package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.UpdatedDevices;
import inz.gameadvisor.restapi.service.DevicesService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<Object> getDevicesOfLoggedInUser(@ApiIgnore @RequestHeader("Authorization") String token,
                                                            HttpServletRequest request){
        return devicesService.getDevicesOfLoggedInUser(token, request);
    }

    @GetMapping("/api/device/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No device found")
    })
    public ResponseEntity<Object> getDevicesByUserID(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                            @RequestParam(defaultValue = "deviceID") String sortBy,
                                                            @PathVariable("id") long id,
                                                            HttpServletRequest request){
        return devicesService.getDevicesByUserID(pageNumber, pageSize, sortBy, id, request);
    }

    @PostMapping("/api/device/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No such PC part found"),
            @ApiResponse(responseCode = "409", description = "Data conflict")
    })
    public ResponseEntity<Object> addDevice(@RequestBody UpdatedDevices addDevice,
                                            HttpServletRequest request,
                                            @ApiIgnore @RequestHeader("Authorization") String token){
        return devicesService.addDevice(addDevice,request,token);
    }

    @PutMapping(value = "/api/device/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No such device found"),
            @ApiResponse(responseCode = "409", description = "Data conflict")
    })
    public ResponseEntity<Object> editDevice(@RequestBody UpdatedDevices editDevice,
                                             @PathVariable("id") long id,
                                             HttpServletRequest request,
                                             @ApiIgnore @RequestHeader("Authorization") String token){
        return devicesService.editDevice(editDevice,request ,id ,token);
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
}

package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.deviceOriented.DevicesUpdated;
import inz.gameadvisor.restapi.service.DevicesService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final DevicesService devicesService;

    @GetMapping("/api/device/admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "No devices found")
    })
    public ResponseEntity<List<Devices>> getAllDevicesList(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "deviceID") String sortBy,
            @ApiIgnore @RequestHeader("Authorization") String token){
        List<Devices> list = devicesService.getAllDevicesList(pageNumber,pageSize,sortBy,token);
        return new ResponseEntity<List<Devices>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/api/device/user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No device found")
    })
    public List<Devices> getDevicesByCurrentUserID(@ApiIgnore @RequestHeader("Authorization") String token) {
        return devicesService.getDevicesByCurrentUserID(token);
    }

    @PostMapping("/api/device/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void createNewDevice(@RequestBody DevicesUpdated device,
                                @ApiIgnore @RequestHeader("Authorization") String token){
        devicesService.createNewDevice(device, token);
    }
}

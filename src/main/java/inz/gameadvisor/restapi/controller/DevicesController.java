package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.Devices;
import inz.gameadvisor.restapi.repository.DevicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final DevicesRepository devicesRepository;

    @GetMapping("/api/device/")
    public List<Devices> getAllDevicesList(){
        return null;
    }
}

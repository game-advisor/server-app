package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.Device;
import inz.gameadvisor.restapi.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceRepository deviceRepository;

    @GetMapping("/api/device/")
    public List<Device> getAllDevicesList(){
        return null;
    }
}

package inz.gameadvisor.restapi.model.gameOriented;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameAndDevicesCompatible {
    private Game game;
    private List<Devices> compatibleDevices;
}

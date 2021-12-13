package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedDevices {
    private String shortName;

    private long cpuID;

    private long gpuID;

    private long ramID;

    private long osID;

    private boolean isHDD;

    private boolean isSSD;
}

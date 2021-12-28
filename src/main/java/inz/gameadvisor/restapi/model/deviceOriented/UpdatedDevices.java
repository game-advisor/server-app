package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedDevices {
    private String shortName;

    private long cpuID;

    private long gpuID;

    private int size;
    private int amountOfSticks;
    private int freq;
    private int latency;

    private long osID;

    private boolean isHDD;

    private boolean isSSD;
}

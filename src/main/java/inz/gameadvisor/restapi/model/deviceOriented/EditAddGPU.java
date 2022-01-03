package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditAddGPU {
    private String name;
    private long manufID;
    private String series;
    private float score;
}

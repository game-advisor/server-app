package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCPU {
    private String name;
    private long manufID;
    private String series;
    private int score;
}

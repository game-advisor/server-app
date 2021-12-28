package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditAddRAM {
    private int size;
    private int amountOfSticks;
    private int freq;
    private int latency;
    private int score;
}

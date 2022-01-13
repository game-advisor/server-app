package inz.gameadvisor.restapi.model.gameOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequirementsPass {
    private boolean cpuOK;
    private boolean gpuOK;
    private boolean osOK;
    private boolean ramSizeOK;
}

package inz.gameadvisor.restapi.model.gameOriented;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameAndGameReqPass {
    private Game game;
    private GameRequirementsPass gameRequirementsPass;
}

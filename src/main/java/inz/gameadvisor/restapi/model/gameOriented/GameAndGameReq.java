package inz.gameadvisor.restapi.model.gameOriented;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameAndGameReq {
    private Game game;
    private GameRequirements gameRequirements;
}

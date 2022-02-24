package inz.gameadvisor.restapi.model.gameOriented;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GameRequirementsList {
    private Game game;
    private List<GameRequirements> gameRequirementsList;
}

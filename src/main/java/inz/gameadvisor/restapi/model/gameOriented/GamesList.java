package inz.gameadvisor.restapi.model.gameOriented;

import inz.gameadvisor.restapi.model.Companies;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GamesList {
    private String companyName;
    private List<Game> gameList;
}

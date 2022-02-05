package inz.gameadvisor.restapi.model.gameOriented;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyAndGamesAndTags {
    private String companyName;
    private Game game;
    private List<Tag> tagList;
}

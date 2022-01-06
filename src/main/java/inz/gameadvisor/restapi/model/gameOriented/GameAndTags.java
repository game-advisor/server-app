package inz.gameadvisor.restapi.model.gameOriented;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inz.gameadvisor.restapi.model.Companies;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GameAndTags {
    private Game game;
    private List<Tag> tags;
}

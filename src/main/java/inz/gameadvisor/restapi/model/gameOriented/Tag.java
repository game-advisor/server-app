package inz.gameadvisor.restapi.model.gameOriented;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inz.gameadvisor.restapi.model.userOriented.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagID;

    private String name;

    @ManyToMany(mappedBy = "favTags")
    @JsonIgnore
    Set<User> likeTags;

    @ManyToMany(mappedBy = "gameTags")
    @JsonIgnore
    Set<Game> gameHasTags;
}

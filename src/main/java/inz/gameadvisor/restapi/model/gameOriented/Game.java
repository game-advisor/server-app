package inz.gameadvisor.restapi.model.gameOriented;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.userOriented.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameID;

    private String name;

    @ManyToOne
    @JoinColumn(name = "devID", referencedColumnName = "companyID")
    private Companies company;

    private String imagePath;

    @Column(name = "publishDate")
    private Date publishDate;

    @ManyToMany(mappedBy = "likedGames")
    @JsonIgnore
    Set<User> like;
}

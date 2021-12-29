package inz.gameadvisor.restapi.model.userOriented;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.gameOriented.Tag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(columnDefinition = "varchar(64)",nullable = false)
    @NotNull
    private String username;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String password;

    @Column(columnDefinition = "varchar(16)",nullable = false)
    @NotNull
    private @JsonIgnore boolean enabled;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String email;

    @Column(name= "avatarPath",columnDefinition = "varchar(255) default 'img/defaultAvatar64x64.png'",nullable = false)
    @NotNull
    private @JsonIgnore String avatarPath;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "favgames",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "gameID")
    )
    @JsonIgnore
    Set<Game> likedGames;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "favtags",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "tagID")
    )
    @JsonIgnore
    Set<Tag> favTags;
}

package inz.gameadvisor.restapi.model.reviewOriented;

import inz.gameadvisor.restapi.model.gameOriented.Game;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewID;

    @JoinColumn(name = "review_userID",referencedColumnName = "userID")
    private long reviewUserID;

    private String content;

    @Column(name = "createdAt")
    private Timestamp dateCreated;

    @ManyToOne
    @JoinColumn(name = "reviewScoreID",referencedColumnName = "scoreID")
    private Score score;

    @ManyToOne
    @JoinColumn(name = "reviewGameID", referencedColumnName = "gameID")
    private Game game;

}

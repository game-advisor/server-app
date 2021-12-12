package inz.gameadvisor.restapi.model.reviewOriented;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    private Date dateCreated;

    @JoinColumn(name = "reviewScoreID",referencedColumnName = "scoreID")
    private long reviewScoreID;

    @JoinColumn(name = "gameID", referencedColumnName = "gameID")
    private long gameID;

}

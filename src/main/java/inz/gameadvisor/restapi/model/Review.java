package inz.gameadvisor.restapi.model;

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
    private long review_userID;

    private String content;

    @Column(name = "createdAt")
    private Date dateCreated;

    @JoinColumn(name = "review_scoreID",referencedColumnName = "scoreID")
    private long review_scoreID;


}

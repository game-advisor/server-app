package inz.gameadvisor.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long scoreID;

    @Column(name = "avgFPS")
    private float avgFPS;

    private int musicRating;

    private int graphicsRating;

    private int gameplayRating;
}
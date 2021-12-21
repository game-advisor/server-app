package inz.gameadvisor.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
}

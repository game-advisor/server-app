package inz.gameadvisor.restapi.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "gpu")
public class GPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gpuID;

    private String name;

    @JoinColumn(name = "manufID", referencedColumnName = "companyID")
    private long manufID;

    private String series;

    private int score;
}

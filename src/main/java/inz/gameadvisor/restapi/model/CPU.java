package inz.gameadvisor.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cpu")
public class CPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cpuID;

    private String name;

    @JoinColumn(name = "manufID", referencedColumnName = "companyID")
    private long manufID;

    private String series;

    private int score;
}

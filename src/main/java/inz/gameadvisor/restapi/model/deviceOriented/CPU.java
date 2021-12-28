package inz.gameadvisor.restapi.model.deviceOriented;

import inz.gameadvisor.restapi.model.Companies;
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

    @ManyToOne
    @JoinColumn(name = "manufID", referencedColumnName = "companyID")
    private Companies company;

    private String series;

    private int score;
}

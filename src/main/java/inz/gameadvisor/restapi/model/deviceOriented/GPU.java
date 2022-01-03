package inz.gameadvisor.restapi.model.deviceOriented;


import inz.gameadvisor.restapi.model.Companies;
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

    @ManyToOne
    @JoinColumn(name = "manufID", referencedColumnName = "companyID")
    private Companies company;

    private String series;

    private float score;
}

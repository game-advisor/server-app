package inz.gameadvisor.restapi.model.deviceOriented;

import inz.gameadvisor.restapi.model.Companies;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "os")
public class OS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long osID;

    private String name;

    @ManyToOne
    @JoinColumn(name = "manufID", referencedColumnName = "companyID")
    private Companies company;
}

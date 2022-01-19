package inz.gameadvisor.restapi.model.gameOriented;

import inz.gameadvisor.restapi.model.deviceOriented.CPU;
import inz.gameadvisor.restapi.model.deviceOriented.GPU;
import inz.gameadvisor.restapi.model.deviceOriented.OS;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "gamerequirements")
public class GameRequirements {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long gameRequirementsID;

    private String shortName;

    private String type;

    @ManyToOne
    @JoinColumn(name = "cpuID",referencedColumnName = "cpuID")
    private CPU cpu;

    @ManyToOne
    @JoinColumn(name = "gpuID",referencedColumnName = "gpuID")
    private GPU gpu;

    @ManyToOne
    @JoinColumn(name = "osID",referencedColumnName = "osID")
    private OS os;

    @ManyToOne
    @JoinColumn(name = "gameID",referencedColumnName = "gameID")
    private Game game;

    private long diskSizeReq;

    private long ramSizeReq;
}

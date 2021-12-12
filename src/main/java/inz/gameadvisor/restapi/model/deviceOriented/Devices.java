package inz.gameadvisor.restapi.model.deviceOriented;

import inz.gameadvisor.restapi.model.userOriented.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "devices")
public class Devices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long deviceID;

    private String shortName;

    @ManyToOne
    @JoinColumn(name = "cpuID", referencedColumnName = "cpuID")
    private CPU cpu;

    @ManyToOne
    @JoinColumn(name = "gpuID", referencedColumnName = "gpuID")
    private GPU gpu;

    @ManyToOne
    @JoinColumn(name = "ramID", referencedColumnName = "ramID")
    private RAM ram;

    @ManyToOne
    @JoinColumn(name = "osID", referencedColumnName = "osID")
    private OS os;

    private boolean isHDD;

    private boolean isSSD;

    @ManyToOne
    @JoinColumn(name="userID", referencedColumnName = "userID")
    private User user;
}

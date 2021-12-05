package inz.gameadvisor.restapi.model;

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

    private long cpuID;

    private long gpuID;

    private long ramID;

    private long osID;

    private boolean isHDD;

    private boolean isSSD;

    @JoinColumn(name="devices_userID", referencedColumnName = "userID")
    private long userID;
}

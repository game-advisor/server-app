package inz.gameadvisor.restapi.model.deviceOriented;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "ram")
public class RAM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ramID;

    //As in GB
    private int size;

    //As how many sticks in PC you got
    private int amountOfSticks;

    private int freq;

    private int latency;
}

package inz.gameadvisor.restapi.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "authorities")
public class Authorities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private String username;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private String authority;

    @Override
    public String toString() {
        return "Authorities{" +
                "authority='" + authority + '\'' +
                '}';
    }
}

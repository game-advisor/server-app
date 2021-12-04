package inz.gameadvisor.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(columnDefinition = "varchar(64)",nullable = false)
    @NotNull
    private String username;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String password;

    @Column(columnDefinition = "varchar(16)",nullable = false)
    @NotNull
    private @JsonIgnore boolean enabled;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String email;

    @Column(name= "avatarPath",columnDefinition = "varchar(255) default 'img/defaultAvatar64x64.png'",nullable = false)
    @NotNull
    private @JsonIgnore String avatarPath;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String roles;
}

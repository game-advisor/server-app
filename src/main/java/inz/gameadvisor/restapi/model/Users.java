package inz.gameadvisor.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "users")
public class Users {
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
    private @JsonIgnore String enabled;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    @NotNull
    private @JsonIgnore String email;

    @Column(name= "avatarPath",columnDefinition = "varchar(255) default 'img/defaultAvatar64x64.png'",nullable = false)
    @NotNull
    private String avatarPath;
}

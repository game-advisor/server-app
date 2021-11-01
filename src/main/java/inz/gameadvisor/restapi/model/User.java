package inz.gameadvisor.restapi.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.NotFound;
import org.springframework.lang.Nullable;
import javax.persistence.*;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(columnDefinition = "varchar(48)",nullable = false)
    @NotNull
    private String login;

    @Column(columnDefinition = "varchar(128)",nullable = false)
    @NotNull
    private String pwd;

    @Column(columnDefinition = "varchar(250)",nullable = false)
    @NotNull
    private String email;

    @Column(columnDefinition = "varchar(128) default 'img/defaultAvatar64x64.png'",nullable = false)
    @NotNull
    private String avatarPath;
}

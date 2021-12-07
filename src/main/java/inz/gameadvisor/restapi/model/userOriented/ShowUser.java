package inz.gameadvisor.restapi.model.userOriented;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowUser {
    private String username;
    private String avatarPath;
    private String roles;
}

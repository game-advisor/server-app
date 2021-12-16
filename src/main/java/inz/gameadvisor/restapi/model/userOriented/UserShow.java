package inz.gameadvisor.restapi.model.userOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShow {
    private long userID;
    private String username;
    private String email;
    private String avatarPath;
}

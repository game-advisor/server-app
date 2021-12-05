package inz.gameadvisor.restapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUser {
    private long updateUserID;

    private String username;

    private String password;
}

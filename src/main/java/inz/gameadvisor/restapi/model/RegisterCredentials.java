package inz.gameadvisor.restapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCredentials {
    private String username;
    private String email;
    private String password;
}

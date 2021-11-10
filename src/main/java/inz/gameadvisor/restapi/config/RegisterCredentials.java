package inz.gameadvisor.restapi.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCredentials {
    private String username;
    private String email;
    private String password;
}

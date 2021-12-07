package inz.gameadvisor.restapi.model.userOriented;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCredentials {
    private String email;
    private String password;
}

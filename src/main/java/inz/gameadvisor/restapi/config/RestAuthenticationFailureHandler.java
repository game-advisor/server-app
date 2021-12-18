package inz.gameadvisor.restapi.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Date date = new Date(System.currentTimeMillis());
        String timestamp = date.toString();
        response.setContentType("application/json");
        response.getWriter()
                .print(
                        "{\n"
                                + "\"message\" : \"Unauthorized\",\n"
                                + "\"code\" : 401,\n"
                                + "\"path\" : \"/api/user/login\",\n"
                                + "\"timestamp\" : \""  + timestamp + "\"\n"
                                + "}");
        response.setStatus(401);
    }
}

package inz.gameadvisor.restapi.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateFormatted = simpleDateFormat.format(date);
        response.setContentType("application/json");
        response.getWriter()
                .print(
                        "{\n"
                                + "\"message\" : \"Unauthorized\",\n"
                                + "\"code\" : 401,\n"
                                + "\"path\" : \"/api/user/login\",\n"
                                + "\"timestamp\" : \""  + dateFormatted + "\"\n"
                                + "}");
        response.setStatus(401);
    }
}

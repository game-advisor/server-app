package inz.gameadvisor.restapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.LoginCredentials;
import lombok.SneakyThrows;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonObjectAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public JsonObjectAuthenticationFilter(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws UsernameNotFoundException {
        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            LoginCredentials authRequest = objectMapper.readValue(sb.toString(), LoginCredentials.class);
            String email = authRequest.getEmail();
            String password = authRequest.getPassword();
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password);
            setDetails(request, token);
            return this.getAuthenticationManager().authenticate(token);
        }
        catch (IOException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (InternalAuthenticationServiceException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}

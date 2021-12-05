package inz.gameadvisor.restapi.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final long expirationTime;
    private final String secret;

    public RestAuthenticationSuccessHandler(@Value("${jwt.expirationTime}") long expirationTime, @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.secret = secret;
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        Query query = em.createNativeQuery("SELECT userID FROM users WHERE username = ?");
        query.setParameter(1,principal.getUsername());
        Integer userID = Integer.parseInt(query.getSingleResult().toString());

        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withClaim("userID", userID)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
        response.addHeader("Authorization",token);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"token\" : "
                + "\"" + token + "\"}"
        );
    }
}

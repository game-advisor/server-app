package inz.gameadvisor.restapi.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import inz.gameadvisor.restapi.misc.MyUserDetails;
import inz.gameadvisor.restapi.misc.MyUserDetailsService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private final MyUserDetailsService userDetailsService;
    private final String secret;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MyUserDetailsService userDetailsService, String secret) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = null;
        try{
            authentication = getAuthentication(request);
        } catch (TokenExpiredException e) {
            final String expiredMsg = e.getMessage();
            logger.warn(expiredMsg);
            final String msg = (expiredMsg != null) ? expiredMsg : "Unauthorized";
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
            return;
        }
        catch(NoSuchElementException e1)
        {
            final String noSuchElementMSG = e1.getMessage();
            final String msg = (noSuchElementMSG != null) ? noSuchElementMSG :  "Using token of non-existent user. Are you sure this is ok?";
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
            return;
        }

        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws TokenExpiredException {
        String token = request.getHeader(TOKEN_HEADER);
        String userName;
        if (token != null) {
            userName = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token) // 5
                    .getSubject(); // 6
            if (userName != null) {
                MyUserDetails MyUserDetails = (inz.gameadvisor.restapi.misc.MyUserDetails) userDetailsService.loadUserByUsername(userName);
                return new UsernamePasswordAuthenticationToken(MyUserDetails.getUsername(), null, MyUserDetails.getAuthorities()); // 8
            }
        }
        return null;
    }
}

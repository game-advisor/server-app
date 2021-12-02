package inz.gameadvisor.restapi.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

        private static final String TOKEN_HEADER = "Authorization";
        private final UserDetailsService userDetailsService;
        private final String secret;

        public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                      UserDetailsService userDetailsService,
                                      String secret) {
            super(authenticationManager);
            this.userDetailsService = userDetailsService;
            this.secret = secret;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws IOException, ServletException {
            UsernamePasswordAuthenticationToken authentication = null;
            try{
                authentication = getAuthentication(request); // 1
            } catch (TokenExpiredException e) {
                final String expiredMsg = e.getMessage();
                logger.warn(expiredMsg);

                final String msg = (expiredMsg != null) ? expiredMsg : "Unauthorized";
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
            }

            if (authentication == null) {
                filterChain.doFilter(request, response);
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication); // 2
            filterChain.doFilter(request, response);
        }

        @SneakyThrows
        private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
            String token = request.getHeader(TOKEN_HEADER); // 3
            String userName;
            if (token != null) {
                userName = JWT.require(Algorithm.HMAC256(secret)) // 4
                        .build()
                        .verify(token) // 5
                        .getSubject(); // 6
                if (userName != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // 7
                        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities()); // 8
                    }
            }
            return null;
        }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public class UserUsedExpiredToken extends NotFoundException {
        public UserUsedExpiredToken(String message)
        {
            super(message);
        }
    }
}

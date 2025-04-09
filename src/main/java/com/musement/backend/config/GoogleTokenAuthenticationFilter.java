package com.musement.backend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class GoogleTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final UserRepository userRepository;
    private final GoogleConfig googleConfig;
    public GoogleTokenAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.googleConfig = new GoogleConfig();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){
            String idToken = authHeader.substring(7);
            try {
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(), GsonFactory.getDefaultInstance())
                        .setAudience(Collections.singletonList(googleConfig.getClientId()))
                        .build();

                GoogleIdToken token = verifier.verify(idToken);
                if (token != null){
                    GoogleIdToken.Payload payload = token.getPayload();
                    User user = userRepository.findUserByEmail(payload.getEmail()).orElse(null);
                    if (user != null){
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user.getUsername(), null, Collections.emptyList());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.enotes.security;

import com.enotes.utils.service.impl.TokenBlockServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TokenBlockServiceImpl tokenBlockService;

    @Autowired
    private CustomUserDetailService customUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Extract the JWT token from Authorization header
        // 2. Validate the token
        // 3. If valid, set authentication in SecurityContext
        // 4. If invalid, optionally block or continue
        // 5. Proceed with the filter chain

        //1
        //here token is received in header
        String authheader = request.getHeader("Authorization");
        String token = null;
        String userName = null;


//		String token = authHeader.replace("Bearer ", "").trim();
        // Example: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        if(authheader != null && authheader.startsWith("Bearer ")) {
            token = authheader.replace("Bearer ", "").trim(); // Use trim to remove accidental spaces

            //2 Check if the token is in the blocklist
            if (tokenBlockService.isTokenBlocked(token)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted. Please login again.");
                return;
            }

            //3 Extract username from the token
//            userName = jwtService.extractUserName(token);
            try {
                userName = jwtService.extractUserName(token);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired JWT token");
                return;
            }

        }

        //4 Validate the token and set authentication
        // Only proceed if:
        //   - token has a username, role and active status
        //   - no user is already authenticated in the context
        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {

            // Load user details from DB
            UserDetails userDetails = customUserDetailService
                    .loadUserByUsername(userName);

            // Implement validateToken  method in jwtService class
            // Validate the token against user details
            if(jwtService.validateToken(token,userDetails)) {

                // Create authentication object
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set request details (e.g., IP, session info)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }

        // 5. Continue the request
        // Let Spring continue to next filter/controller
        filterChain.doFilter(request, response);
    }
}

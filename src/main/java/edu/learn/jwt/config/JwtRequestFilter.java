package edu.learn.jwt.config;

import edu.learn.jwt.auth.services.JwtUserDetailService;
import edu.learn.jwt.auth.utils.JwtUtils;
import edu.learn.jwt.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtUserDetailService jwtUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        isNotAuthRequest(request)
                .map(b ->
                        extractToken(request)
                                .map(token ->
                                        Try.of(() -> jwtUtils.getUserFromToken(token)
                                                .filter(username -> jwtUtils.validateToken(token, username).get())
                                                .onEmpty(() -> {
                                                    throw new TokenInvalid();
                                                })
                                                .filter(username -> SecurityContextHolder.getContext().getAuthentication() == null)
                                                .peek((username) -> {

                                                    UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);

                                                    UsernamePasswordAuthenticationToken authenticationToken =
                                                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                                }))
                                                .recover(this::handleExceptions)
                                )
                );

        filterChain.doFilter(request, response);
    }

    private Option<HttpServletRequest> isNotAuthRequest(HttpServletRequest request) {
        return Option.of(request.getRequestURI().startsWith("/auth") ? null : request);
    }

    private Option<String> extractToken(HttpServletRequest httpServletRequest) {
        String tokenHeader = httpServletRequest.getHeader("Authorization");

        return Option.of(
                StringUtils.isNotBlank(tokenHeader) && tokenHeader.startsWith("Bearer ") ?
                        tokenHeader.substring(7)
                        : EMPTY);
    }

    private Option<String> handleExceptions(Throwable ex) {
        if (ex instanceof UserNotFound) {
            LOGGER.error("User not found", ex);
        } else if (ex instanceof UserNotInTokenException) {
            LOGGER.error("User not present in the token", ex);
        } else if (ex instanceof UserInTokenInvalid) {
            LOGGER.error("User from token is not valid", ex);
        } else if (ex instanceof SignatureException) {
            LOGGER.error("Invalid token", ex);
        } else if (ex instanceof ExpiredJwtException ||
                ex instanceof TokenExpired) {
            LOGGER.error("Expired token", ex);
        } else {
            LOGGER.error(ex);
        }

        return Option.of(EMPTY);
    }
}

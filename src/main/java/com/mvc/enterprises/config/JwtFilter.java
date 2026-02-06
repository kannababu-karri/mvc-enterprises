package com.mvc.enterprises.config;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mvc.enterprises.utils.JwtUtil;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private static final Logger _LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
    	
    	String uri = request.getRequestURI();
    	
    	_LOGGER.info("Incoming request: " + uri);
    	
    	 //Allow login & static resources
        if (uri.equals("/ILabs/login")
        		|| uri.equals("/ILabs/loginHome")
        		|| uri.equals("/ILabs/logout")
                || uri.startsWith("/ILabs/styles/")
                || uri.startsWith("/ILabs/scripts/")
                || uri.startsWith("/ILabs/images/")) {
        	
        	_LOGGER.info("Inside if loop: " + uri);

            chain.doFilter(request, response);
            return;
        }
        
        _LOGGER.info("Not in if condition");
        /*
        HttpSession session = request.getSession(false);
        String token = (session != null) ? 
                (String) session.getAttribute(Utils.getTokenKey()) : null;

        _LOGGER.info("JwtFilter: token:"+token);
        
        if (token != null && jwtUtil.validateToken(token)) {
        	_LOGGER.info("JwtFilter: inside token true");
            chain.doFilter(request, response);
        } else if (request.getRequestURI().equals("/login")) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect("loginHome");
        } */
        HttpSession session = request.getSession(false);
        String token = (session != null) ? 
                (String) session.getAttribute(Utils.getTokenKey()) : null;

        _LOGGER.info("JwtFilter: token:"+token);

        if (token != null && jwtUtil.validateToken(token)) {
        	_LOGGER.info("JwtFilter: inside token true");

            //Extract username & roles from token
            String username = jwtUtil.extractUsername(token);
            _LOGGER.info("JwtFilter: inside token username:"+username);
            List<String> roles = jwtUtil.extractRoles(token);
            _LOGGER.info("JwtFilter: inside token roles:"+roles);

            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                         .map(SimpleGrantedAuthority::new)
                         .toList();
            //Create Authentication
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
            //Set into SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        } else if (request.getRequestURI().equals("/login")) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect("loginHome");
        }
    }
    
    /*
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        _LOGGER.info("Incoming request: " + uri);

        // allow login & static resources
        if (uri.equals("/ILabs/login")
        		|| uri.equals("/ILabs/loginHome")
        		|| uri.equals("/ILabs/logout")
                || uri.startsWith("/ILabs/styles/")
                || uri.startsWith("/ILabs/scripts/")
                || uri.startsWith("/ILabs/images/")) {
        	_LOGGER.info("Allowed endpoint, passing through filter: " + uri);
            chain.doFilter(request, response);
            return;
        }
        
        _LOGGER.info("Not in if condition");

        // read JWT from Authorization header
        String authHeader = request.getHeader("Authorization");
        _LOGGER.info("authHeader: " + authHeader);
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ?
                authHeader.substring(7) : null;
        
        _LOGGER.info("token: " + token);

        if (token != null && jwtUtil.validateToken(token)) {
            chain.doFilter(request, response);
        } else {
            // return 401 instead of redirect
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: JWT token missing or invalid");
        }
    } */
}

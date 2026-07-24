package com.amdox.nexushr.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Value("${jwt.secret}")
    private String secret;



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)

            throws ServletException, IOException {



        String path=request.getServletPath();


        System.out.println("PATH = "+path);



        if(path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")){


            filterChain.doFilter(request,response);
            return;
        }



        String header=request.getHeader("Authorization");


        System.out.println("AUTH HEADER = "+header);



        if(header != null && header.startsWith("Bearer ")) {


            String token=header.substring(7);



            try {

            	Claims claims =
            	        Jwts.parser()
            	        .setSigningKey(getKey())
            	        .parseClaimsJws(token)
            	        .getBody();


                System.out.println(
                        "JWT SUBJECT = "
                        +claims.getSubject()
                );



                UsernamePasswordAuthenticationToken authentication =

                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                Collections.emptyList()
                        );



                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);



                System.out.println(
                        "JWT AUTH SET SUCCESS"
                );



            }
            catch(Exception e){

                System.out.println(
                        "JWT ERROR"
                );

                e.printStackTrace();

            }

        }



        filterChain.doFilter(request,response);

    }



    private SecretKey getKey(){

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

    }

}
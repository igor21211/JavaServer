package com.example.javaserver.services;

import com.example.javaserver.entity.User;
import com.example.javaserver.exceptions.JwtAuthExeption;
import com.example.javaserver.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JwtTokenService implements JwtService{

    private static final String TOKEN_PREFIX = "Bearer ";

    private UserService service;
    private static final long EXPIRE_TIME_S = 120000;
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String createToken(User user){
        UserDetails userDetails = new MyUserDetails(user);
        Claims claims = Jwts.claims()
                .setSubject(userDetails.getUsername());
        claims.put("role",user.getRole());
        claims.put("id", user.getId());
        claims.put("username", user.getEmail());
        claims.put("firstname", user.getName());
        claims.put("lastname", user.getLastname());;

        Date now = new Date();
        Date expire = new Date(now.getTime()+ EXPIRE_TIME_S*24000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(KEY)
                .compact();
    }
    public String resolveToken(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest)
                .map(req->req.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(auth->auth.startsWith(TOKEN_PREFIX))
                .map(auth->auth.substring(7))
                .orElse(null);
    }

    public boolean validateToken(String token) {
        if(token==null) return false;
        try{
            Jws<Claims> jwt = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            if(jwt.getBody().getExpiration().before(new Date())){
                return false;
            }
            return true;
        }catch (Exception e){
            throw new JwtAuthExeption("Invalid token");
        }
    }

    public Authentication createAuthentication(String token) {
        User user = extractUser(token);
        UserDetails userDetails = new MyUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
            userDetails,userDetails.getPassword(),userDetails.getAuthorities()
        );
    }

    public User extractUser(String token) {
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
        String email = jwt.getBody().getSubject();
       return service.getByEmail(email);
    }
}

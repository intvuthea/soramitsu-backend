package com.example.springjava.api;

import com.example.springjava.dtos.LoginRequest;
import com.example.springjava.dtos.UserResponse;
import com.example.springjava.jwt.JwtHelper;
import com.example.springjava.models.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest form) {
        String username = form.getUsername();
        String password = form.getPassword();

        try {
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = this.authenticationManager.authenticate(upat);

            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String token = jwtHelper.generateAccessToken(user.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("duration", Integer.toString(this.jwtHelper.getTokenDuration()));

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity me(@AuthenticationPrincipal User user, HttpServletRequest request) {
        UserResponse userResponse = new UserResponse(user.getUsername(), request.getRemoteAddr());
        Map<String, UserResponse> response = new HashMap<>();
        response.put("user", userResponse);

        return ResponseEntity.ok().body(response);
    }
}

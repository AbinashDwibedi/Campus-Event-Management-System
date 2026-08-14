package com.abinash.campus_management.controller;

import com.abinash.campus_management.dto.MyUserDto;
import com.abinash.campus_management.dto.MyUserLoginDto;
import com.abinash.campus_management.services.JwtService;
import com.abinash.campus_management.services.MyUserDetailsService;
import com.abinash.campus_management.services.MyUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuth {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final MyUserService myUserService;
    private final JwtService jwtService;
    @PostMapping("/register")
    public ResponseEntity<MyUserDto> register(@Valid @RequestBody MyUserDto userDto){
       userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
       MyUserDto registeredUser = myUserService.registerUser(userDto);
       return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public  ResponseEntity<String> login(@Valid @RequestBody MyUserLoginDto userDto, HttpServletResponse response){
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getName(),
                        userDto.getPassword()
                )
        );
        String token = jwtService.generateToken(userDto.getName());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge(24*60*60*10);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");

        response.addCookie(cookie);
        return ResponseEntity.ok("login successfully");
    }
}

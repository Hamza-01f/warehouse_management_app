package com.brief.demo.controller;

import com.brief.demo.dto.request.LoginRequestDTO;
import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.ApiTokenResponse;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.service.UserService;
import com.brief.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ApiTokenResponse<String> login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateAccessToken(userDetails);
       return ResponseEntity.ok(ApiTokenResponse.success(token , " your token is being retrieved with success : ")).getBody()   ;
    }

}

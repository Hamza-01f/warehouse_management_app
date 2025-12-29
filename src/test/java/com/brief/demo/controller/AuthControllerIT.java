//package com.brief.demo.controller;
//
//
//import com.brief.demo.dto.request.LoginRequestDTO;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.MediaType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AuthControllerIT  {
//
//    @Autowired
//    MockMvc mockMvc;
//
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Test
//    void ShouldLogInSuccessfully(){
//        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
//        loginRequestDTO.setEmail("hamza@boumanjel.com");
//        loginRequestDTO.setPassword("123456");
//
//        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON))
//
//    }
//}

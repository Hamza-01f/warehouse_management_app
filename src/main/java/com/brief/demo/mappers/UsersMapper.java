package com.brief.demo.mappers;

import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.model.User;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

import java.awt.*;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    User toEntity(RegisterRequestDTO registerRequestDTO);

    AuthResponseDTO toAuthResponseDTO(User user);
}

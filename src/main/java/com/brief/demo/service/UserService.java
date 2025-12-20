package com.brief.demo.service;


import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.UsersMapper;
import com.brief.demo.model.Roles;
import com.brief.demo.model.User;
import com.brief.demo.model.UserPrincipal;
import com.brief.demo.repository.UserRepository;
import com.brief.demo.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UsersMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        Roles role = userRoleRepository.findById(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found with id: " + request.getRole()
                ));

        user.setUserRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toAuthResponseDTO(savedUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws ResourceNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        return new UserPrincipal(user);


    }
}

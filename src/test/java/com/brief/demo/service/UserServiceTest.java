package com.brief.demo.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserService userService;
//
//    private RegisterRequestDTO registerRequest;
//    private LoginRequestDTO loginRequest;
//    private User user;
//    private AuthResponseDTO authResponse;
//    private Enum role = CLIENT;
//    @BeforeEach
//    void setUp() {
//        registerRequest = new RegisterRequestDTO();
//        registerRequest.setEmail("test@example.com");
//        registerRequest.setPassword("password123");
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setRole(role.);
//
//        loginRequest = new LoginRequestDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password123");
//
//        user = User.builder()
//                .id(1L)
//                .email("test@example.com")
//                .password("hashedPassword")
//                .firstName("John")
//                .lastName("Doe")
//                .role(Role.CLIENT)
//                .isActive(true)
//                .build();
//
//        authResponse = new AuthResponseDTO();
//        authResponse.setId(1L);
//        authResponse.setEmail("test@example.com");
//        authResponse.setMessage("Success");
//    }
//
//    @Test
//    void register_ShouldRegisterUserSuccessfully() {
//        //  ARRANGE
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
//        when(userMapper.toEntity(registerRequest)).thenReturn(user);
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userMapper.toAuthResponse(user, "User registered successfully")).thenReturn(authResponse);
//
//        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
//            bcryptMock.when(() -> BCrypt.hashpw("password123", BCrypt.gensalt()))
//                    .thenReturn("hashedPassword");
//
//            //  ACT
//            AuthResponseDTO result = userService.register(registerRequest);
//
//            //  ASSERT
//            assertNotNull(result);
//            assertEquals(authResponse, result);
//            verify(userRepository, times(1)).existsByEmail("test@example.com");
//            verify(userRepository, times(1)).save(user);
//            verify(userMapper, times(1)).toAuthResponse(user, "User registered successfully");
//        }
//    }
//
//    @Test
//    void register_WhenEmailAlreadyExists_ShouldThrowDuplicateResourceException() {
//        //  ARRANGE
//        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
//
//        //  ACT & ASSERT
//        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
//                () -> userService.register(registerRequest));
//
//        assertEquals("Email already exists", exception.getMessage());
//        verify(userRepository, never()).save(any(User.class));
//    }

//    @Test
//    void login_ShouldLoginUserSuccessfully() {
//        //  ARRANGE
//        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
//        when(userMapper.toAuthResponse(user, "Login successful")).thenReturn(authResponse);
//
//        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
//            bcryptMock.when(() -> BCrypt.checkpw("password123", "hashedPassword"))
//                    .thenReturn(true);
//
//            //  ACT
//            AuthResponseDTO result = userService.login(loginRequest);
//
//            //  ASSERT
//            assertNotNull(result);
//            assertEquals(authResponse, result);
//            verify(userRepository, times(1)).findByEmail("test@example.com");
//            verify(userMapper, times(1)).toAuthResponse(user, "Login successful");
//        }
//    }

//    @Test
//    void login_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
//        //  ARRANGE
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//
//        //  ACT & ASSERT
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> userService.login(loginRequest));
//
//        assertEquals("User not found", exception.getMessage());
//        verify(userRepository, never()).save(any(User.class));
//    }

//    @Test
//    void login_WhenWrongPassword_ShouldThrowUnauthorizedException() {
//        // 🟢 ARRANGE
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
//            bcryptMock.when(() -> BCrypt.checkpw("wrongpassword", "hashedPassword"))
//                    .thenReturn(false);
//
//            // 🟢 ACT & ASSERT
//            UnauthorizedException exception = assertThrows(UnauthorizedException.class,
//                    () -> userService.login(loginRequest));
//
//            assertEquals("Invalid credentials", exception.getMessage());
//        }
//    }

//    @Test
//    void login_WhenUserInactive_ShouldThrowUnauthorizedException() {
//        // 🟢 ARRANGE
//        user.setIsActive(false);
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        try (MockedStatic<BCrypt> bcryptMock = Mockito.mockStatic(BCrypt.class)) {
//            bcryptMock.when(() -> BCrypt.checkpw("password123", "hashedPassword"))
//                    .thenReturn(true);
//
//            // 🟢 ACT & ASSERT
//            UnauthorizedException exception = assertThrows(UnauthorizedException.class,
//                    () -> userService.login(loginRequest));
//
//            assertEquals("Account is deactivated", exception.getMessage());
//        }
//    }

//    @Test
//    void getUserById_ShouldReturnUser() {
//        // 🟢 ARRANGE
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        // 🟢 ACT
//        User result = userService.getUserById(1L);
//
//        // 🟢 ASSERT
//        assertNotNull(result);
//        assertEquals(user, result);
//        verify(userRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void getUserById_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
//        // 🟢 ARRANGE
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // 🟢 ACT & ASSERT
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> userService.getUserById(1L));
//
//        assertEquals("User not found with id: 1", exception.getMessage());
//    }

}
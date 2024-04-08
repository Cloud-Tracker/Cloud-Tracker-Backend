package com.example.cloud_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.cloud_tracker.dto.UserDTO;
import com.example.cloud_tracker.model.JwtResponse;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.repository.UserRepository;

import init.UserInit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        String username = "test@example.com";
        User user = new User();
        user.setEmail(username);
        when(userRepository.findByEmail(username)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    public void testRegister_UserDoesNotExist() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("password");
        userDTO.setName("newUser");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);

        // Act
        User registeredUser = userService.register(userDTO);


        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("existing@example.com");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(new User());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.register(userDTO));
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    public void testLogin_ValidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword("encryptedPassword");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);

        JwtResponse jwtResponse = userService.login(userDTO);

        assertNotNull(jwtResponse);
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
    }

    @Test
    public void testLogin_InvalidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("wrongpassword");
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword("encryptedPassword");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.login(userDTO));
    }

    @Test
    public void testLogin_NullUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword("encryptedPassword");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);
        when(bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);

        verify(jwtService, times(0)).generateToken(user);
        verify(jwtService, times(0)).generateRefreshToken(user);
        assertThrows(IllegalArgumentException.class, () -> userService.login(userDTO));
    }

    @Test
    public void testFindUserByEmail_UserFound() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User foundUser = userService.findUserByEmail(email);

        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    public void testFindUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserByEmail(email));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testSaveProfileImage() {
        String email = "test@example.com";
        String image = "profile_image.jpg";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        userService.saveProfileImage(email, image);

        verify(userRepository, times(1)).save(user);
        assertEquals(image, user.getImage());
    }

    @Test
    public void testGetCurrentUser() {
        // Mock UserDetails
        UserDetails userDetails = new User(
                new UserDTO("test@example.com", "password", null,null)
        );

        // Mock the SecurityContext
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock UserRepository response
        User expectedUser = new User(
                new UserDTO("test@example.com", "password", null,null)
        );
        when(userRepository.findByEmail("test@example.com")).thenReturn(expectedUser);

        //Assertion
        User currentUser = userService.getCurrentUser();
        assertEquals(expectedUser, currentUser);
    }

    @Test
    void testGetCurrentUserName() {
        // Mocking getCurrentUser() method
        User currentUser = new User(
                new UserDTO("test@example.com", "password", "name",null)
        );

        // Create a spy of UserService to partially mock it, allowing us to mock specific methods while keeping the rest intact
        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(currentUser).when(userServiceSpy).getCurrentUser();

        // Testing getCurrentUserName() method
        String name = userServiceSpy.getCurrentUserName();
        assertEquals("name", name);
    }

    @Test
    void testGetCurrentUserProfilePicture() {
        // Mocking getCurrentUser() method
        User currentUser = new User(
                new UserDTO("test@example.com", "password", "name",null)
        );
        currentUser.setImage("img");

        // Create a spy of UserService to partially mock it, allowing us to mock specific methods while keeping the rest intact
        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(currentUser).when(userServiceSpy).getCurrentUser();

        // Testing getCurrentUserName() method
        String img = userServiceSpy.getCurrentUserProfilePicture();
        assertEquals("img", img);
    }

    @Test
    void testGetCurrentUserEmail() {
        // Mocking getCurrentUser() method
        User currentUser = new User(
                new UserDTO("test@example.com", "password", "name",null)
        );

        // Create a spy of UserService to partially mock it, allowing us to mock specific methods while keeping the rest intact
        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(currentUser).when(userServiceSpy).getCurrentUser();

        // Testing getCurrentUserName() method
        String email = userServiceSpy.getCurrentUserEmail();
        assertEquals("test@example.com", email);
    }

    @Test
    public void testUpdateProfileSuccess(){
        UserDTO userUpdateDTO = new UserDTO("test@gmail.com",
        "12345",
        "test",
        "image.jpg");
        User user = UserInit.createUser();

        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(user).when(userServiceSpy).getCurrentUser();

        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(userUpdateDTO.getPassword())).thenReturn(userUpdateDTO.getPassword());
        
        User actualUser = new User(1,userUpdateDTO.getEmail(),userUpdateDTO.getPassword(),userUpdateDTO.getName(),userUpdateDTO.getImage(),null);
        User user2 = userServiceSpy.editProfile(userUpdateDTO);
        assertEquals(actualUser, user2);
    }

    @Test
    public void testUpdateProfileFixedEmail(){
        UserDTO userUpdateDTO = new UserDTO("test@test.com",
        "12345",
        "test",
        "image.jpg");
        User user = UserInit.createUser();

        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(user).when(userServiceSpy).getCurrentUser();

        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userUpdateDTO.getPassword())).thenReturn(userUpdateDTO.getPassword());
        
        User actualUser = new User(1,userUpdateDTO.getEmail(),userUpdateDTO.getPassword(),userUpdateDTO.getName(),userUpdateDTO.getImage(),null);
        User user2 = userServiceSpy.editProfile(userUpdateDTO);
        assertEquals(actualUser, user2);
    }

    @Test
    public void testUpdateProfileFailed(){
        UserDTO userUpdateDTO = new UserDTO("test@gmail.com",
        "12345",
        "test",
        "image.jpg");
        User user = UserInit.createUser();

        UserService userServiceSpy = Mockito.spy(userService);
        Mockito.doReturn(user).when(userServiceSpy).getCurrentUser();
        
        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userUpdateDTO.getPassword())).thenReturn(userUpdateDTO.getPassword());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceSpy.editProfile(userUpdateDTO));
        assertEquals("Email already exists", exception.getMessage());
    }


}

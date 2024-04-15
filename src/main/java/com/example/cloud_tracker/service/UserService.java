package com.example.cloud_tracker.service;

import com.example.cloud_tracker.dto.UserDTO;
import com.example.cloud_tracker.model.JwtResponse;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtService jwtService;

  public UserService(UserRepository userRepository,
                     BCryptPasswordEncoder bCryptPasswordEncoder,
                     JwtService jwtService) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username);
    if (user == null)
      throw new UsernameNotFoundException("User not found");
    return user;
  }

  public User register(@NonNull UserDTO userDTO) {
    if (userRepository.findByEmail(userDTO.getEmail()) != null) {
      throw new IllegalArgumentException("User already exists");
    }
    userDTO.setEmail(userDTO.getEmail().toLowerCase());
    User user = new User(userDTO);
    user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

    return userRepository.save(user);
  }

  public JwtResponse login(@NonNull UserDTO userDTO) {
    userDTO.setEmail(userDTO.getEmail().toLowerCase());
    User user = userRepository.findByEmail(userDTO.getEmail());
    if (user != null && bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
      return new JwtResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }
    throw new IllegalArgumentException("Invalid credentials");
  }

  public User findUserByEmail(String email) {
    if (userRepository.findByEmail(email) == null) {
      throw new IllegalArgumentException("User not found");
    }
    return userRepository.findByEmail(email);
  }

  public void saveProfileImage(String email, String image) {
    User user = findUserByEmail(email);
    user.setImage(image);
    userRepository.save(user);
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userRepository.findByEmail(userDetails.getUsername());
  }

  public String getCurrentUserProfilePicture() {
    User currentUser = getCurrentUser();
    return currentUser.getImage();
  }

  public String getCurrentUserName() {
    User currentUser = getCurrentUser();
    return currentUser.getName();
  }

  public String getCurrentUserEmail() {
    User currentUser = getCurrentUser();
    return currentUser.getEmail();
  }
  
  public User editProfile(UserDTO updateDTO){
    User user = getCurrentUser();
    if (userRepository.findByEmail(updateDTO.getEmail()) != null 
        && !user.getEmail().equals(updateDTO.getEmail())) {
        throw new IllegalArgumentException("Email already exists");
    }
    user.setName(updateDTO.getName());
    user.setEmail(updateDTO.getEmail());
    user.setPassword(bCryptPasswordEncoder.encode(updateDTO.getPassword()));
    user.setImage(updateDTO.getImage());
    userRepository.save(user);
    return user;
   }

}

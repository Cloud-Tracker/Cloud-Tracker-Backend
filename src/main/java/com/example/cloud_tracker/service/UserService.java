package com.example.cloud_tracker.service;

import com.example.cloud_tracker.DTO.UserDTO;
import com.example.cloud_tracker.model.JwtResponse;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<User> user = userRepository.findByEmail(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> register(@NonNull UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User(userDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        try {
            return Optional.of(userRepository.save(user));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public JwtResponse login(@NonNull UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isPresent()
                && bCryptPasswordEncoder.matches(userDTO.getPassword(), userOptional.get().getPassword())){
            return new JwtResponse(jwtService.generateToken(userOptional.get()),
                    jwtService.generateRefreshToken(userOptional.get()));
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("User not found")));
    }

    public void saveProfileImage(String email, String image) {
        User user = getUserByEmail(email).get();
        user.setImage(image);
        userRepository.save(user);
    }
}

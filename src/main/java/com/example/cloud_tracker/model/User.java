package com.example.cloud_tracker.model;

import com.example.cloud_tracker.dto.UserDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(unique = true)
  @NotNull
  private String email;

  @NotNull private String password;
  private String name;
  @Lob private String image;

  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
  @Nullable
  private List<IAMRole> roles;

  public User(UserDTO userDTO) {
    this.email = userDTO.getEmail();
    this.name = userDTO.getName();
    this.password = userDTO.getPassword();
    this.image = userDTO.getImage();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

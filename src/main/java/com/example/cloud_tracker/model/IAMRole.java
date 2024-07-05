package com.example.cloud_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class IAMRole {
  private String accountID;
  private String roleName;
  @Id private String arn;
  @Column(name = "user_id")
  private int userId;

  public IAMRole(String arn) {
    this.arn = arn;
  }

  public IAMRole(String accountID, String roleName, int userId, String arn) {
    this.accountID = accountID;
    this.roleName = roleName;
    this.userId = userId;
    this.arn = arn;
  }

  @PrePersist
  private void setRoleCrendentials() {
    int startIndex = this.arn.indexOf("::") + 2;
    int endIndex = this.arn.indexOf(":", startIndex);

    this.accountID = this.arn.substring(startIndex, endIndex);

    startIndex = this.arn.lastIndexOf("/") + 1;
    this.roleName = this.arn.substring(startIndex);
  }
}

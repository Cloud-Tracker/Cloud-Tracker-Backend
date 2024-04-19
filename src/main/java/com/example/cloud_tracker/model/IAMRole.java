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
  @Id
  private String arn;

  public IAMRole(String accountID, String roleName, int userId) {
    this.accountID = accountID;
    this.roleName = roleName;
    this.userId = userId;
  }

  @Column(name = "user_id")
  private int userId;

  @PrePersist
  private void setArn() {
    this.arn = "arn:aws:iam::" + accountID + ":role/" + roleName;
  }
}

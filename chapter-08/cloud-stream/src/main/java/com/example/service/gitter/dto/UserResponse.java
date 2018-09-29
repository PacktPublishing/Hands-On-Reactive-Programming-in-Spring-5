package com.example.service.gitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private String id;
  private Integer v;
  private String username;
  private String displayName;
  private String avatarUrl;
  private String avatarUrlSmall;
  private String avatarUrlMedium;
  private Role role;
  private boolean staff;
  private String gv;
  private String url;
}

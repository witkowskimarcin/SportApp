package com.example.sportapp.model;

import lombok.Data;

@Data
public class User extends SimpleFirebaseObject {
  String email;
  String firstname;
  String lastname;
  String city;
  String address;
  String country;
  Role role;

  public enum Role {
    USER,
    ADMIN
  }
}

package com.example.sportapp.model;

import java.util.List;

import lombok.Data;

@Data
public class User extends SimpleFirebaseObject {
  String email;
  String firstname;
  String lastname;
  String city;
  String address;
  String country;
  List<BoughtOffer> offers;
  Role role;

  public enum Role {
    USER,
    ADMIN
  }
}

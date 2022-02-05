package com.example.sportapp.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class City implements Serializable {
  String uuid;
  String city;
  String admin_name;
  String country;
  String iso2;
}

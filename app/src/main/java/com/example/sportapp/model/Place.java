package com.example.sportapp.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Place extends SimpleFirebaseObject implements Serializable {
  String city;
  String address;
  String postCode;
  String name;
  String imgBase64;
  List<Carnet> carnets;
}

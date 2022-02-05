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
  String address;
  String description;
  String name;
  String imgBase64;
  String cityId;
  String coordinates;
  List<String> categoryIds;
  List<Offer> offers;

  public double getLatitude() {
    return Double.parseDouble(coordinates.substring(0, coordinates.indexOf(',')));
  }

  public double getLongitude() {
    return Double.parseDouble(coordinates.substring(coordinates.indexOf(',') + 1));
  }
}

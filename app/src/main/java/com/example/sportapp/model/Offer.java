package com.example.sportapp.model;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Offer extends SimpleFirebaseObject implements Serializable {
  Integer price;
  String type;
  Integer value;
  String title;
  String description;
  String imgBase64;
  String categoryId;
  Place place;
  Boolean timesOn;
  Map<String, String> times;
  BoughtOffer boughtOffer;

  public String getTypePL() {
    if ("MONTH".equals(type)) {
      return "miesiąc";
    } else if ("YEAR".equals(type)) {
      return "rok";
    } else if ("DAY".equals(type)) {
      return "dni";
    } else {
      return type;
    }
  }
}

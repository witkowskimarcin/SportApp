package com.example.sportapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

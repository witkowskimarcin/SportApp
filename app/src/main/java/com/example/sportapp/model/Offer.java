package com.example.sportapp.model;

import java.io.Serializable;

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
}

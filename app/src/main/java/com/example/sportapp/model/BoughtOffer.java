package com.example.sportapp.model;

import lombok.Data;

@Data
public class BoughtOffer extends SimpleFirebaseObject {
  String offerId;
  String placeId;
  String startDate;
  String endDate;
}

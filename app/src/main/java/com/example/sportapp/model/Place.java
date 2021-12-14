package com.example.sportapp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place extends SimpleFirebaseObject {
    String city;
    String address;
    String postCode;
    String name;
    List<Carnet> carnets;
}

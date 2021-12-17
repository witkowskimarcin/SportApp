package com.example.sportapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Carnet extends SimpleFirebaseObject {
    Integer price;
    String type;
    Integer value;
}

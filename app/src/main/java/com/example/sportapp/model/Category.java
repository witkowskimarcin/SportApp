package com.example.sportapp.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Category implements Serializable {
    String uuid;
    String name;
}

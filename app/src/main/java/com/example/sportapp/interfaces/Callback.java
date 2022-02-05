package com.example.sportapp.interfaces;

import com.example.sportapp.model.Category;
import com.example.sportapp.model.City;

import java.util.List;

public interface Callback {
  void onData(List<City> list1, List<Category> list2);
}

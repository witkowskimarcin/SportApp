package com.example.sportapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sportapp.R;
import com.example.sportapp.interfaces.Callback;
import com.example.sportapp.model.Category;
import com.example.sportapp.model.City;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.FragmentService;
import com.example.sportapp.service.PlaceService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment {
  private static final String TAG = "SearchFragment";
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final FragmentService fragmentService = FragmentService.getInstance();
  private final PlaceService placeService = PlaceService.getInstance();

  private Spinner spinnerCity;
  private Spinner spinnerCategory;

  private List<Place> places;
  private List<City> cities;
  private List<Category> categories;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);

    View root = inflater.inflate(R.layout.fragment_home, container, false);
//    getAllPlaces();
    final Button buttonFind = root.findViewById(R.id.button_find);

    spinnerCity = root.findViewById(R.id.spinner_city);
    spinnerCategory = root.findViewById(R.id.spinner_categories);
    buttonFind.setEnabled(false);

    getData(
        (cities, categories) -> {
          ArrayAdapter<String> adapterCities =
              new ArrayAdapter<String>(
                  getContext(),
                  android.R.layout.simple_spinner_item,
                  cities.stream().map(City::getCity).collect(Collectors.toList()));
          spinnerCity.setAdapter(adapterCities);

          ArrayAdapter<String> adapterCategory =
              new ArrayAdapter<String>(
                  getContext(),
                  android.R.layout.simple_spinner_item,
                  categories.stream().map(Category::getName).collect(Collectors.toList()));
          spinnerCategory.setAdapter(adapterCategory);

          buttonFind.setEnabled(true);
        });

    buttonFind.setOnClickListener(
        v -> {
          City city =
              cities.stream()
                  .filter(_city -> _city.getCity().equals((String) spinnerCity.getSelectedItem()))
                  .findAny()
                  .get();
          Category category =
              categories.stream()
                  .filter(
                      _category ->
                          _category.getName().equals((String) spinnerCategory.getSelectedItem()))
                  .findAny()
                  .get();

          if (city != null
              && city.getCity().length() > 0
              && category != null
              && category.getName().length() > 0) {
            Toast.makeText(getContext(), city.getCity(), Toast.LENGTH_SHORT).show();

            Bundle args = new Bundle();
            args.putSerializable("city", city);
            args.putSerializable("category", category);

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, PlacesFragment.class, args)
                .setReorderingAllowed(true)
                .addToBackStack("PlacesFragment") // name can be null
                .commit();
          }
        });

    return root;
  }

  public void getAllPlaces() {
    Task<QuerySnapshot> querySnapshotTask =
        db.collection("places")
            .get()
            .addOnCompleteListener(
                task -> {
                  if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                      List<Place> _places = new ArrayList<>();
                      List<DocumentSnapshot> documents = task.getResult().getDocuments();
                      for (DocumentSnapshot document : documents) {
                        Place place = document.toObject(Place.class);
                        place.setUuid(document.getId());
//                        getAllCarnetsFromPlace(place);
                        _places.add(place);
                      }
                      places = _places;
                      System.out.println("places "+places.size());
                    }
                  } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                  }
                });
  }

//  public void getAllCarnetsFromPlace(Place place) {
//    Task<QuerySnapshot> querySnapshotTask =
//        db.collection("places")
//            .document(place.getUuid())
//            .collection("carnets")
//            .get()
//            .addOnCompleteListener(
//                task -> {
//                  if (task.isSuccessful()) {
//                    if (task.getResult() != null) {
//                      List<Offer> _offers = new ArrayList<>();
//                      List<DocumentSnapshot> documents = task.getResult().getDocuments();
//                      for (DocumentSnapshot document : documents) {
//                        Offer offer = document.toObject(Offer.class);
//                        offer.setUuid(document.getId());
//                        _offers.add(offer);
//                      }
//                      place.setCarnets(_offers);
//
//                      System.out.println("Do");
//                    }
//                  } else {
//                    Log.w(TAG, "Error getting documents.", task.getException());
//                  }
//                });
//  }

  private void getData(Callback callback) {
    Task<QuerySnapshot> task1 = db.collection("cities").get();
    Task<QuerySnapshot> task2 = db.collection("categories").get();

    Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
    allTasks.addOnCompleteListener(
        task -> {
          if (task.isSuccessful()) {
            QuerySnapshot query1 = (QuerySnapshot) task1.getResult();
            QuerySnapshot query2 = (QuerySnapshot) task2.getResult();

            cities = new ArrayList<>();
            categories = new ArrayList<>();

            for (DocumentSnapshot document : query1) {
              if (document.exists()) {
                City city = document.toObject(City.class);
                city.setUuid(document.getId());
                cities.add(city);
              }
            }
            for (DocumentSnapshot document : query2) {
              if (document.exists()) {
                Category category = document.toObject(Category.class);
                category.setUuid(document.getId());
                categories.add(category);
              }
            }

            Collections.sort(cities, Comparator.comparing(City::getCity));
            Collections.sort(categories, Comparator.comparing(Category::getName));

            callback.onData(cities, categories);
          }
        });
  }
}

package com.example.sportapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.Adapters.PlacesInfoViewAdapter;
import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.Category;
import com.example.sportapp.model.City;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {
  private static final String TAG = "PlacesFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  private RecyclerView infoRecyclerView;
  private PlacesInfoViewAdapter placesInfoViewAdapter;

  private final AuthenticationService authenticationService = AuthenticationService.getInstance();

  private FragmentService fragmentService = FragmentService.getInstance();

  private List<Place> places;
  private City city;
  private Category category;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);

    View root = inflater.inflate(R.layout.fragment_places, container, false);

    if (getArguments() != null) {
      city = (City) getArguments().getSerializable("city");
      category = (Category) getArguments().getSerializable("category");
    }

    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.components(view);

    infoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    getAllPlaces();
  }

  private void components(View view) {
    infoRecyclerView = view.findViewById(R.id.infoRecyclerView);
  }

  public void getAllPlaces() {
    Task<QuerySnapshot> querySnapshotTask =
        db.collection("places")
            .whereEqualTo("cityId", city.getUuid())
            .whereArrayContains("categoryIds", category.getUuid())
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
                        _places.add(place);
                      }
                      System.out.println("Do");
                      places = _places;

                      placesInfoViewAdapter =
                          new PlacesInfoViewAdapter(
                              places,
                              new ClickListener() {
                                @Override
                                public void onPositionClicked(int position) {
                                  Toast.makeText(
                                          getContext(),
                                          places.get(position).getName(),
                                          Toast.LENGTH_SHORT)
                                      .show();

                                  Bundle args = new Bundle();
                                  args.putSerializable("place", places.get(position));
                                  args.putSerializable("category", category);
                                  FragmentManager fragmentManager = getParentFragmentManager();
                                  fragmentManager
                                      .beginTransaction()
                                      .replace(R.id.nav_host_fragment, PlaceFragment.class, args)
                                      .setReorderingAllowed(true)
                                      .addToBackStack("PlaceFragment") // name can be null
                                      .commit();
                                }

                                @Override
                                public void onLongClicked(int position) {}
                              },
                              getContext());
                      infoRecyclerView.setAdapter(placesInfoViewAdapter);
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
  //                new OnCompleteListener<QuerySnapshot>() {
  //                  @Override
  //                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
  //                    if (task.isSuccessful()) {
  //                      if (task.getResult() != null) {
  //                        List<Offer> _offers = new ArrayList<>();
  //                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
  //                        for (DocumentSnapshot document : documents) {
  //                          Offer offer = document.toObject(Offer.class);
  //                          offer.setUuid(document.getId());
  //                          _offers.add(offer);
  //                        }
  //                        place.setOffers(_offers);
  //                        placesInfoViewAdapter =
  //                            new PlacesInfoViewAdapter(
  //                                places,
  //                                new ClickListener() {
  //                                  @Override
  //                                  public void onPositionClicked(int position) {
  //                                    Toast.makeText(
  //                                            getContext(),
  //                                            places.get(position).getName(),
  //                                            Toast.LENGTH_SHORT)
  //                                        .show();
  //
  //                                    Bundle args = new Bundle();
  //                                    args.putSerializable("place", places.get(position));
  //                                    FragmentManager fragmentManager =
  // getParentFragmentManager();
  //                                    fragmentManager
  //                                        .beginTransaction()
  //                                        .replace(R.id.nav_host_fragment, PlaceFragment.class,
  // args)
  //                                        .setReorderingAllowed(true)
  //                                        .addToBackStack("PlaceFragment") // name can be null
  //                                        .commit();
  //                                  }
  //
  //                                  @Override
  //                                  public void onLongClicked(int position) {}
  //                                },
  //                                getContext());
  //                        infoRecyclerView.setAdapter(placesInfoViewAdapter);
  //                      }
  //                    } else {
  //                      Log.w(TAG, "Error getting documents.", task.getException());
  //                    }
  //                  }
  //                });
  //  }
}

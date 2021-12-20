package com.example.sportapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.sportapp.R;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.FragmentService;
import com.example.sportapp.service.PlaceService;
import com.example.sportapp.ui.PlacesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
  private static final String TAG = "HomeFragment";
  List<Place> places;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final FragmentService fragmentService = FragmentService.getInstance();
  private final PlaceService placeService = PlaceService.getInstance();
  private HomeViewModel homeViewModel;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);

    homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    View root = inflater.inflate(R.layout.fragment_home, container, false);
    //        final TextView textView = root.findViewById(R.id.text_home);
    //        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
    //            @Override
    //            public void onChanged(@Nullable String s) {
    //                textView.setText(s);
    //            }
    //        });

    //        placeService.getPlacesByCity("Lodz");
    getAllPlaces();
    final Button buttonFind = root.findViewById(R.id.button_find);
    final EditText editTextCity = root.findViewById(R.id.text_city);

    buttonFind.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View v) {
            String city = editTextCity.getText().toString();
            if (city.length() > 0) {
              Toast.makeText(getContext(), city, Toast.LENGTH_SHORT).show();

              FragmentManager fragmentManager = getParentFragmentManager();
              fragmentManager
                  .beginTransaction()
                  .replace(R.id.nav_host_fragment, PlacesFragment.class, null)
                  .setReorderingAllowed(true)
                  .addToBackStack("name") // name can be null
                  .commit();
            }
          }
        });

    return root;
  }

  public void getAllPlaces() {
    Task<QuerySnapshot> querySnapshotTask =
        db.collection("places")
            .get()
            .addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                      if (task.getResult() != null) {
                        List<Place> _places = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                          Place place = document.toObject(Place.class);
                          place.setUuid(document.getId());
                          getAllCarnetsFromPlace(place);
                          _places.add(place);
                        }
                        System.out.println("Do");
                        places = _places;
                      }
                    } else {
                      Log.w(TAG, "Error getting documents.", task.getException());
                    }
                  }
                });
  }

  public void getAllCarnetsFromPlace(Place place) {
    Task<QuerySnapshot> querySnapshotTask =
        db.collection("places")
            .document(place.getUuid())
            .collection("carnets")
            .get()
            .addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                      if (task.getResult() != null) {
                        List<Offer> _offers = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                          Offer offer = document.toObject(Offer.class);
                          offer.setUuid(document.getId());
                          _offers.add(offer);
                        }
                        place.setCarnets(_offers);

                        System.out.println("Do");
                      }
                    } else {
                      Log.w(TAG, "Error getting documents.", task.getException());
                    }
                  }
                });
  }
}

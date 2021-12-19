package com.example.sportapp.ui;

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
import com.example.sportapp.model.Carnet;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.example.sportapp.ui.gallery.GalleryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
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

  private GalleryViewModel galleryViewModel;

  private List<Place> places;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.layout.fragment_places);

    //        if (authenticationService.isAuthenticated()) {
//    galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
    View root = inflater.inflate(R.layout.fragment_places, container, false);
    //            final TextView textView = root.findViewById(R.id.text_gallery);
    //            galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
    // {
    //                @Override
    //                public void onChanged(@Nullable String s) {
    //                    textView.setText(s);
    //                }
    //            });
    return root;
    //        }
    //
    //        // wroc do logowania
    //        NavController navController = Navigation.findNavController(getActivity(),
    // R.id.nav_host_fragment);
    //        navController.navigate(R.id.nav_login);
    //        return null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.components(view);

    infoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    getAllPlaces();
    //        infoViewAdapter = new InfoViewAdapter(places, new ClickListener() {
    //            @Override
    //            public void onPositionClicked(int position) {
    ////                Intent intent = new Intent(getActivity(), FoodSelection.class);
    ////                intent.putExtra("pasta", (Serializable) foodArrayList.get(position));
    ////                startActivity(intent);
    //
    ////                OsobaFragment fragment = new OsobaFragment();
    ////                FragmentTransaction transaction =
    // getActivity().getSupportFragmentManager().beginTransaction();
    ////                transaction.replace(R.id.nav_host_fragment, fragment);
    ////                transaction.addToBackStack(null);
    ////                transaction.commit();
    //            }
    //
    //            @Override
    //            public void onLongClicked(int position) {
    //
    //            }
    //        }, getContext());
    //        infoRecyclerView.setAdapter(infoViewAdapter);
  }

  private void components(View view) {
    infoRecyclerView = view.findViewById(R.id.infoRecyclerView);
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
                        List<Carnet> _carnets = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                          Carnet carnet = document.toObject(Carnet.class);
                          carnet.setUuid(document.getId());
                          _carnets.add(carnet);
                        }
                        place.setCarnets(_carnets);
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
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    fragmentManager
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment, PlaceFragment.class, args)
                                        .setReorderingAllowed(true)
                                        .addToBackStack("name") // name can be null
                                        .commit();

                                    //                Intent intent = new Intent(getActivity(),
                                    // FoodSelection.class);
                                    //                intent.putExtra("pasta", (Serializable)
                                    // foodArrayList.get(position));
                                    //                startActivity(intent);

                                    //                OsobaFragment fragment = new OsobaFragment();
                                    //                FragmentTransaction transaction =
                                    // getActivity().getSupportFragmentManager().beginTransaction();
                                    //                transaction.replace(R.id.nav_host_fragment,
                                    // fragment);
                                    //                transaction.addToBackStack(null);
                                    //                transaction.commit();
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
                  }
                });
  }
}

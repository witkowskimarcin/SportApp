package com.example.sportapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.Adapters.OffersInfoViewAdapter;
import com.example.sportapp.Adapters.PostionsInfoViewAdapter;
import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.BoughtOffer;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyPositionsFragment extends Fragment {
  private static final String TAG = "MyPositionsFragment";
  private final AuthenticationService authenticationService = AuthenticationService.getInstance();
  private final FragmentService fragmentService = FragmentService.getInstance();
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private LinearLayout linearLayout;
  private OffersInfoViewAdapter offersInfoViewAdapter;
  private List<Place> places;
  private RecyclerView recyclerView;
  private List<Offer> offers = new ArrayList<>();
  Map<String, Offer> uniqueOffers = new HashMap<>();

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_my_positions);

    if (authenticationService.isAuthenticated()) {
      places = new ArrayList<>();
      Observer observer = (observable, o) -> setPlaces();
      authenticationService.addObserver(observer);
      authenticationService.fetchUserInfo();

      View root = inflater.inflate(R.layout.fragment_my_positions, container, false);
      return root;
    }

    // wroc do logowania
    NavController navController =
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    navController.navigate(R.id.nav_login);
    return null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    components(view);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    super.onCreate(savedInstanceState);
  }

  private void setPlaces() {
    List<BoughtOffer> boughtOffers = authenticationService.getUser().getOffers();
    List<String> placesIds =
        boughtOffers.stream()
            .map(boughtOffer -> boughtOffer.getPlaceId())
            .collect(Collectors.toList());
    List<String> offersIds =
        authenticationService.getUser().getOffers().stream()
            .map(boughtOffer -> boughtOffer.getOfferId())
            .collect(Collectors.toList());
    if (placesIds.size() > 0) {
      db.collection("places")
          .whereIn(FieldPath.documentId(), placesIds)
          .get()
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  if (task.getResult() != null) {
                    List<Place> _places = new ArrayList<>();
                    List<Offer> _offers = new ArrayList<>();
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    List<Offer> allOffers = new ArrayList<>();
                    for (DocumentSnapshot document : documents) {
                      Place place = document.toObject(Place.class);
                      place.setUuid(document.getId());
                      //                      getOffers(place);
                      _places.add(place);

                      //                      List<Offer> __offers =
                      //                          place.getOffers().stream()
                      //                              .filter(
                      //                                  offer -> {
                      //                                    offer.setPlaceUuid(place.getUuid());
                      //                                    return
                      // offersIds.contains(offer.getUuid());
                      //                                  })
                      //                              .collect(Collectors.toList());
                      //                      _offers.addAll(__offers);

                      //                      _places.forEach(
                      //                          _place -> {
                      //                            List<Offer> __offers =
                      //                                _place.getOffers().stream()
                      //                                    .filter(
                      //                                        offer -> {
                      //
                      // offer.setPlaceUuid(_place.getUuid());
                      //                                          return
                      // offersIds.contains(offer.getUuid());
                      //                                        })
                      //                                    .collect(Collectors.toList());
                      //                            _offers.addAll(__offers);
                      //                          });
                      //                      _offers.forEach(offer ->
                      // uniqueOffers.put(offer.getUuid(), offer));

                      _places.forEach(
                          _place -> {
                            _place
                                .getOffers()
                                .forEach(
                                    offer -> {
                                      Optional<BoughtOffer> optionalBoughtOffer =
                                          boughtOffers.stream()
                                              .filter(
                                                  boughtOffer ->
                                                      boughtOffer
                                                          .getOfferId()
                                                          .equals(offer.getUuid()))
                                              .findAny();
                                      if (optionalBoughtOffer.isPresent()) {
                                        offer.setBoughtOffer(optionalBoughtOffer.get());
                                      }

                                      offer.setPlace(_place);
                                      allOffers.add(offer);
                                    });
                          });
                    }
                    System.out.println("Do");
                    places = _places;
                    this.offers = _offers;
                    //                                        uniqueOffers = new HashMap<>();
                    //                    _offers.forEach(offer -> uniqueOffers.put(offer.getUuid(),
                    // offer));
                    //                    offers = new ArrayList<>(uniqueOffers.values());

                    allOffers.forEach(offer -> uniqueOffers.put(offer.getUuid(), offer));
                    this.offers =
                        (new ArrayList<>(uniqueOffers.values()))
                            .stream()
                                .filter(offer -> offersIds.contains(offer.getUuid()))
                                .collect(Collectors.toList());

                    setRecyclerViewAdapter();
                  }
                } else {
                  Log.w(TAG, "Error getting documents.", task.getException());
                }
              });
    }
  }

  //  private void getOffers(Place place) {
  //    List<String> offersIds =
  //        authenticationService.getUser().getOffers().stream()
  //            .map(BoughtOffer::getOfferId)
  //            .collect(Collectors.toList());
  //    Task<QuerySnapshot> querySnapshotTask =
  //        db.collection("places")
  //            .document(place.getUuid())
  //            .collection("carnets")
  //            .whereIn(FieldPath.documentId(), offersIds)
  //            .get()
  //            .addOnCompleteListener(
  //                task -> {
  //                  if (task.isSuccessful()) {
  //                    offers = new ArrayList<>();
  //                    List<Offer> _offers = new ArrayList<>();
  //                    if (task.getResult() != null) {
  //                      List<DocumentSnapshot> documents = task.getResult().getDocuments();
  //                      for (DocumentSnapshot document : documents) {
  //                        Offer offer = document.toObject(Offer.class);
  //                        offer.setUuid(document.getId());
  //                        BoughtOffer boughtOffer =
  //                            authenticationService.getUser().getOffers().stream()
  //                                .filter(o -> o.getOfferId().equals(offer.getUuid()))
  //                                .findFirst()
  //                                .get();
  //                        offer.setDescription("Ważne do: " + boughtOffer.getEndDate());
  //                        offer.setImgBase64(place.getImgBase64());
  //                        _offers.add(offer);
  //                        offers.add(offer);
  //                      }
  ////                      offers = _offers;
  //                      place.setOffers(offers);
  //                      setRecyclerViewAdapter();
  //                    }
  //                  } else {
  //                    Log.w(TAG, "Error getting documents.", task.getException());
  //                  }
  //                });
  //  }

  private void setRecyclerViewAdapter() {
    PostionsInfoViewAdapter offersInfoViewAdapter =
        new PostionsInfoViewAdapter(
            offers,
            new ClickListener() {
              @Override
              public void onPositionClicked(int position) {
                Toast.makeText(getContext(), offers.get(position).getTitle(), Toast.LENGTH_SHORT)
                    .show();
                Offer _offer = offers.get(position);
                Place _place = _offer.getPlace();
//                    places.stream()
//                        .filter(place -> place.getUuid().equals(_offer.getPlaceUuid()))
//                        .findAny()
//                        .get();
                Bundle args = new Bundle();
                args.putSerializable("place", _place);
                args.putSerializable("offer", _offer);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, PositionFragment.class, args)
                    .setReorderingAllowed(true)
                    .addToBackStack("PositionFragment") // name can be null
                    .commit();
              }

              @Override
              public void onLongClicked(int position) {}
            },
            getContext());
    recyclerView.setAdapter(offersInfoViewAdapter);
  }

  private void components(View view) {
    recyclerView = view.findViewById(R.id.infoRecyclerView);
  }
}

package com.example.sportapp.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportapp.R;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

public class PositionFragment extends Fragment {
  private static final String TAG = "PositionFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private AuthenticationService authenticationService = AuthenticationService.getInstance();
  private FragmentService fragmentService = FragmentService.getInstance();

  private Place place;
  private Offer offer;
  private TextView title;
  private TextView description;
  private ImageView image;
  private LinearLayout linearLayout;
  private LinearLayout linearLayout2;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);
    View root = inflater.inflate(R.layout.fragment_position, container, false);

    if (getArguments() != null) {
      offer = (Offer) getArguments().getSerializable("offer");
      place = (Place) getArguments().getSerializable("place");
      this.components(root);

      if (offer.getTimesOn() && offer.getTimes() != null && offer.getTimes().size() > 0) {
        String time = offer.getTimes().get("monday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Poniedziałek" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("tuesday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Wtorek" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("wednesday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Środa" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("thursday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Czwartek" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("friday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Piątek" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("saturday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Sobota" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        time = offer.getTimes().get("sunday");
        if (StringUtils.isNotBlank(time)) {
          TextView textView = new TextView(root.getContext());
          textView.setText("Niedziela" + " - " + time);
          textView.setGravity(Gravity.CENTER);
          textView.setTextSize(30.0f);
          linearLayout2.addView(textView);
        }
        Button addToCalendarButton =
            root.findViewById(R.id.buttonAddToCalendar);
        // addToCalendarButton.setVisibility(View.VISIBLE);
        addToCalendarButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                Toast.makeText(getContext(), "Dodano do kalendarza", Toast.LENGTH_SHORT).show();
              }
            });
      }
    }

    SupportMapFragment supportMapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
    supportMapFragment.getMapAsync(
        new OnMapReadyCallback() {
          @Override
          public void onMapReady(GoogleMap googleMap) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(place.getName());
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            googleMap.addMarker(markerOptions);

            googleMap.setOnMapClickListener(
                new GoogleMap.OnMapClickListener() {
                  @Override
                  public void onMapClick(LatLng latLng) {
                    //                    Toast.makeText(getContext(), "Map click",
                    // Toast.LENGTH_SHORT).show();
                  }
                });
          }
        });

    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  private void components(View view) {
    title = view.findViewById(R.id.name);
    description = view.findViewById(R.id.description);
    image = view.findViewById(R.id.photo);
    linearLayout = view.findViewById(R.id.linearLayout);
    linearLayout2 = view.findViewById(R.id.linearLayout2);

    title.setText(offer.getTitle() + " ważne do: " + offer.getBoughtOffer().getEndDate());
    description.setText(offer.getDescription());
    if (StringUtils.isNotBlank(place.getImgBase64())) {
      byte[] decodedString = Base64.decode(place.getImgBase64(), Base64.DEFAULT);
      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
      image.setImageBitmap(decodedByte);
    }
  }
}

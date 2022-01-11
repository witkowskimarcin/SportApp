package com.example.sportapp.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sportapp.R;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.firebase.firestore.FirebaseFirestore;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class OfferFragment extends Fragment {
  private static final String TAG = "OfferFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private AuthenticationService authenticationService = AuthenticationService.getInstance();
  private FragmentService fragmentService = FragmentService.getInstance();

  private Place place;
  private Offer offer;
  private TextView title;
  private TextView description;
  private ImageView image;
  private LinearLayout linearLayout;
  private Button buttonBuy;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);
    View root = inflater.inflate(R.layout.fragment_offer, container, false);

    if (getArguments() != null) {
      offer = (Offer) getArguments().getSerializable("offer");
      place = (Place) getArguments().getSerializable("place");
    }
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.components(view);
  }

  private void components(View view) {
    title = view.findViewById(R.id.name);
    description = view.findViewById(R.id.description);
    image = view.findViewById(R.id.photo);
    linearLayout = view.findViewById(R.id.linearLayout);
    buttonBuy = view.findViewById(R.id.button_buy);

    title.setText(offer.getTitle());
    description.setText(
        (offer.getPrice() / 100) + " zł za " + offer.getValue() + " " + offer.getTypePL());
    if (StringUtils.isNotBlank(place.getImgBase64())) {
      byte[] decodedString = Base64.decode(place.getImgBase64(), Base64.DEFAULT);
      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
      image.setImageBitmap(decodedByte);
    }

    boolean canBuyOffer =
        authenticationService.isAuthenticated()
            && authenticationService.getUser().getOffers() != null
            && authenticationService.getUser().getOffers().stream()
                .filter(_offer -> _offer.getOfferId().equals(offer.getUuid()))
                .findAny()
                .isPresent();
    if (canBuyOffer) {
      buttonBuy.setVisibility(View.GONE);
    }

    buttonBuy.setOnClickListener(
        view1 -> {
          if (authenticationService.isAuthenticated()) {
            Map<String, Object> request = new HashMap<>();
            request.put("offerId", offer.getUuid());
            request.put("placeId", place.getUuid());
            request.put("startDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            if ("MONTH".equals(offer.getType())) {
              request.put(
                  "endDate",
                  OffsetDateTime.now()
                      .plusMonths(offer.getValue())
                      .format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else if ("YEAR".equals(offer.getType())) {
              request.put(
                  "endDate",
                  OffsetDateTime.now()
                      .plusYears(offer.getValue())
                      .format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
              request.put(
                  "endDate",
                  OffsetDateTime.now()
                      .plusDays(offer.getValue())
                      .format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            db.collection("users")
                .document(authenticationService.getUser().getEmail())
                .collection("savedPositions")
                .document()
                .set(request)
                .addOnSuccessListener(aVoid -> {
                  Log.d(TAG, "DocumentSnapshot successfully written!");
                  Toast.makeText(getContext(), "Transakcja udana.", Toast.LENGTH_SHORT).show();
                  buttonBuy.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                  Log.w(TAG, "Error writing document", e);
                  Toast.makeText(getContext(), "Transakcja nieudana, spróbuj później.", Toast.LENGTH_SHORT).show();
                });
          } else {
            Toast.makeText(
                    getContext(), "Musisz być zalogowany aby dokonać zakupu", Toast.LENGTH_SHORT)
                .show();
          }
        });
  }
}

package com.example.sportapp.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sportapp.service.PaypalService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferFragment extends Fragment {
  private static final String TAG = "OfferFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private AuthenticationService authenticationService = AuthenticationService.getInstance();
  private FragmentService fragmentService = FragmentService.getInstance();
  private PaypalService paypalService;

  private Place place;
  private Offer offer;
  private TextView title;
  private TextView description;
  private TextView toBuyText;
  private ImageView image;
  private LinearLayout linearLayout;
  private PaymentButton payPalButton;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);
    View root = inflater.inflate(R.layout.fragment_offer, container, false);
    paypalService.getInstance(getActivity().getApplication());

    if (getArguments() != null) {
      offer = (Offer) getArguments().getSerializable("offer");
      place = (Place) getArguments().getSerializable("place");
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
    this.components(view);
  }

  private void components(View view) {
    toBuyText = view.findViewById(R.id.textViewToBuy);
    title = view.findViewById(R.id.name);
    description = view.findViewById(R.id.description);
    image = view.findViewById(R.id.photo);
    linearLayout = view.findViewById(R.id.linearLayout);
    payPalButton = view.findViewById(R.id.payPalButton);

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
      payPalButton.setVisibility(View.GONE);
      toBuyText.setText("Posiadasz już ten karnet");
    }

    if (authenticationService.isAuthenticated()) {
      toBuyText.setText("Aby dokonać zakupu wybierz jedną z opcji płatności:");
    } else {
      payPalButton.setVisibility(View.GONE);
      Toast.makeText(getContext(), "Musisz być zalogowany, aby dokonać zakupu", Toast.LENGTH_SHORT)
          .show();
    }

    payPalButton.setup(
        new CreateOrder() {
          @Override
          public void create(@NotNull CreateOrderActions createOrderActions) {
            ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(
                new PurchaseUnit.Builder()
                    .amount(
                        new Amount.Builder()
                            .currencyCode(CurrencyCode.PLN)
                            .value(String.valueOf(offer.getPrice() / 100))
                            .build())
                    .description(offer.getTitle())
                    .build());
            Order order =
                new Order(
                    OrderIntent.CAPTURE,
                    new AppContext.Builder().userAction(UserAction.PAY_NOW).build(),
                    purchaseUnits);
            createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
          }
        },
        new OnApprove() {
          @Override
          public void onApprove(@NotNull Approval approval) {
            approval
                .getOrderActions()
                .capture(
                    new OnCaptureComplete() {
                      @Override
                      public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                        savePayment();
                        Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                      }
                    });
          }
        });
  }

  private void savePayment() {
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
          OffsetDateTime.now().plusDays(offer.getValue()).format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    db.collection("users")
        .document(authenticationService.getUser().getEmail())
        .collection("savedPositions")
        .document()
        .set(request)
        .addOnSuccessListener(
            aVoid -> {
              Log.d(TAG, "DocumentSnapshot successfully written!");
              Toast.makeText(getContext(), "Transakcja udana.", Toast.LENGTH_SHORT).show();
              payPalButton.setVisibility(View.GONE);
              toBuyText.setText("Posiadasz już ten karnet");
            })
        .addOnFailureListener(
            e -> {
              Log.w(TAG, "Error writing document", e);
              Toast.makeText(
                      getContext(), "Transakcja nieudana, spróbuj później.", Toast.LENGTH_SHORT)
                  .show();
            });
  }
}

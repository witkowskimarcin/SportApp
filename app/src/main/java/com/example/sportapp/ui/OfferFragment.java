package com.example.sportapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportapp.R;
import com.example.sportapp.model.Offer;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.FragmentService;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

public class OfferFragment extends Fragment {
  private static final String TAG = "OfferFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  private FragmentService fragmentService = FragmentService.getInstance();

  private Place place;
  private Offer offer;
  private TextView title;
  private TextView description;
  private ImageView image;
  private LinearLayout linearLayout;

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

    title.setText(offer.getTitle());
    description.setText(offer.getDescription());
    if (StringUtils.isNotBlank(place.getImgBase64())) {
      byte[] decodedString = Base64.decode(place.getImgBase64(), Base64.DEFAULT);
      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
      image.setImageBitmap(decodedByte);
    }
  }

  private void components(View view) {
    title = view.findViewById(R.id.name);
    description = view.findViewById(R.id.description);
    image = view.findViewById(R.id.photo);
    linearLayout = view.findViewById(R.id.linearLayout);
  }
}

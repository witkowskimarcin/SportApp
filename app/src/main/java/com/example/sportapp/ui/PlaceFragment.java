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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.Adapters.OffersInfoViewAdapter;
import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

public class PlaceFragment extends Fragment {
  private static final String TAG = "PlaceFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  private RecyclerView infoRecyclerView;
  private OffersInfoViewAdapter offersInfoViewAdapter;

  private final AuthenticationService authenticationService = AuthenticationService.getInstance();

  private FragmentService fragmentService = FragmentService.getInstance();

  private Place place;
  private TextView title;
  private TextView description;
  private ImageView image;
  private LinearLayout linearLayout;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_home);

    //        if (authenticationService.isAuthenticated()) {
    //    galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
    View root = inflater.inflate(R.layout.fragment_place, container, false);
    //            final TextView textView = root.findViewById(R.id.text_gallery);
    //            galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
    // {
    //                @Override
    //                public void onChanged(@Nullable String s) {
    //                    textView.setText(s);
    //                }
    //            });

    if (getArguments() != null) {
      place = (Place) getArguments().getSerializable("place");
    }

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

    title.setText(place.getName());
    description.setText(place.getAddress() + " " + place.getPostCode());
    if (StringUtils.isNotBlank(place.getImgBase64())) {
      byte[] decodedString = Base64.decode(place.getImgBase64(), Base64.DEFAULT);
      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
      image.setImageBitmap(decodedByte);
    }

    RecyclerView carnets = new RecyclerView(getContext());
//    carnets.setId(69);
    carnets.setLayoutParams(
        new RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    linearLayout.addView(carnets);
    carnets.setLayoutManager(new LinearLayoutManager(getContext()));

    offersInfoViewAdapter =
        new OffersInfoViewAdapter(
            place.getCarnets(),
            new ClickListener() {
              @Override
              public void onPositionClicked(int position) {
                Toast.makeText(
                        getContext(),
                        place.getCarnets().get(position).getTitle(),
                        Toast.LENGTH_SHORT)
                    .show();

                                        Bundle args = new Bundle();
                args.putSerializable("place", place);
                args.putSerializable("offer", place.getCarnets().get(position));
                                        FragmentManager fragmentManager =
                 getParentFragmentManager();
                                        fragmentManager
                                                .beginTransaction()
                                                .replace(R.id.nav_host_fragment,
                 OfferFragment.class, args)
                                                .setReorderingAllowed(true)
                                                .addToBackStack("OfferFragment") // name can be null
                                                .commit();
              }

              @Override
              public void onLongClicked(int position) {}
            },
            getContext());
    carnets.setAdapter(offersInfoViewAdapter);
  }

  private void components(View view) {
    title = view.findViewById(R.id.name);
    description = view.findViewById(R.id.description);
    image = view.findViewById(R.id.photo);
    linearLayout = view.findViewById(R.id.linearLayout);
  }
}

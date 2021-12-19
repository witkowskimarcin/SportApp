package com.example.sportapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.Adapters.OffersInfoViewAdapter;
import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.Place;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.example.sportapp.ui.gallery.GalleryViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceFragment extends Fragment {
  private static final String TAG = "PlaceFragment";
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  private RecyclerView infoRecyclerView;
  private OffersInfoViewAdapter offersInfoViewAdapter;

  private final AuthenticationService authenticationService = AuthenticationService.getInstance();

  private FragmentService fragmentService = FragmentService.getInstance();

  private GalleryViewModel galleryViewModel;

  private Place place;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.layout.fragment_place);

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

    infoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

                //                        Bundle args = new Bundle();
                //                        args.putSerializable("place", places.get(position));
                //                        FragmentManager fragmentManager =
                // getParentFragmentManager();
                //                        fragmentManager
                //                                .beginTransaction()
                //                                .replace(R.id.nav_host_fragment,
                // PlaceFragment.class, args)
                //                                .setReorderingAllowed(true)
                //                                .addToBackStack("name") // name can be null
                //                                .commit();
              }

              @Override
              public void onLongClicked(int position) {}
            },
            getContext());
    infoRecyclerView.setAdapter(offersInfoViewAdapter);
  }

  private void components(View view) {
    infoRecyclerView = view.findViewById(R.id.infoRecyclerView);
  }
}

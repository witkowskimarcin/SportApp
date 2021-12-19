package com.example.sportapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.sportapp.R;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;

public class GalleryFragment extends Fragment {
  private final AuthenticationService authenticationService = AuthenticationService.getInstance();

  private final FragmentService fragmentService = FragmentService.getInstance();

  private GalleryViewModel galleryViewModel;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentService.setLastFragment(R.id.nav_gallery);

    if (authenticationService.isAuthenticated()) {
      galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
      View root = inflater.inflate(R.layout.fragment_gallery, container, false);
      final TextView textView = root.findViewById(R.id.text_gallery);
      galleryViewModel
          .getText()
          .observe(
              getViewLifecycleOwner(),
              new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                  textView.setText(s);
                }
              });
      return root;
    }

    // wroc do logowania
    NavController navController =
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    navController.navigate(R.id.nav_login);
    return null;
  }
}

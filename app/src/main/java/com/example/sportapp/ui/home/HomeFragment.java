package com.example.sportapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sportapp.R;
import com.example.sportapp.service.FragmentService;

public class HomeFragment extends Fragment {
    private FragmentService fragmentService = FragmentService.getInstance();

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentService.setLastFragment(R.id.nav_home);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        final Button buttonFind = root.findViewById(R.id.button_find);
        final EditText editTextCity = root.findViewById(R.id.text_city);

        buttonFind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String city = editTextCity.getText().toString();
                if(city.length()>0) {
                    Toast.makeText(getContext(), city,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}
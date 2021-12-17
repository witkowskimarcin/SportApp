package com.example.sportapp.interfaces;

import androidx.fragment.app.Fragment;

public interface NavigationListener {
    void changeFragment(Fragment fragment, Boolean addToBackStack);
}

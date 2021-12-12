package com.example.sportapp.service;

import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sportapp.R;
import com.google.android.material.navigation.NavigationView;

public class FragmentService {
    private AppBarConfiguration appBarConfiguration;

    private int lastFragment;

    private static FragmentService instance;

    public static FragmentService getInstance() {
        if (instance == null)
            instance = new FragmentService();
        return instance;
    }

    private FragmentService() {
    }

    public static void setInstance(FragmentService instance) {
        FragmentService.instance = instance;
    }

    public void setAppBarConfiguration(AppBarConfiguration mAppBarConfiguration) {
        this.appBarConfiguration = mAppBarConfiguration;
    }

    public void navigateToLogin(FragmentActivity activity) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(R.id.nav_slideshow);
    }

    public void navigateToLastFragment(FragmentActivity activity){
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(lastFragment);
    }

    public AppBarConfiguration getAppBarConfiguration() {
        return appBarConfiguration;
    }

    public int getLastFragment() {
        return lastFragment;
    }

    public void setLastFragment(int lastFragment) {
        this.lastFragment = lastFragment;
    }
}

package com.example.sportapp;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

  private AppBarConfiguration mAppBarConfiguration;

  public FirebaseUser user;
  private FragmentService fragmentService = FragmentService.getInstance();

  private AuthenticationService authenticationService = AuthenticationService.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    FirebaseApp.initializeApp(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);
    mAppBarConfiguration =
        new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_my_positions, R.id.nav_login)
            .setDrawerLayout(drawer)
            .build();

    authenticationService.setHeaderView(navigationView.getHeaderView(0));
    fragmentService.setAppBarConfiguration(mAppBarConfiguration);
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
        || super.onSupportNavigateUp();
  }
}

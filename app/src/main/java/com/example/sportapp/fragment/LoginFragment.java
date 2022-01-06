package com.example.sportapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.sportapp.R;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
  private static final String TAG = "EmailPassword";
  private final AuthenticationService authenticationService = AuthenticationService.getInstance();
  // [END declare_auth]
  private final FragmentService fragmentService = FragmentService.getInstance();
  Button buttonLogin;
  Button buttonRegister;
  EditText editTextEmail;
  EditText editTextPassword;
  // [START declare_auth]
  private FirebaseAuth mAuth;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    FirebaseApp.initializeApp(getContext());
    mAuth = FirebaseAuth.getInstance();

    if (!authenticationService.isAuthenticated()) {
      View root = inflater.inflate(R.layout.fragment_login, container, false);
      buttonLogin = root.findViewById(R.id.buttonLogin);
      buttonRegister = root.findViewById(R.id.buttonRegister);
      editTextEmail = root.findViewById(R.id.editTextEmail);
      editTextPassword = root.findViewById(R.id.editTextPassword);

      buttonLogin.setOnClickListener(
          v -> signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString()));

      buttonRegister.setOnClickListener(v -> register());

      return root;
    }

    editAccount();
    return null;
  }

  private void register() {
    FragmentManager fragmentManager = getParentFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.nav_host_fragment, RegisterFragment.class, null)
        .setReorderingAllowed(true)
        .addToBackStack("RegisterFragment") // name can be null
        .commit();
  }

  private void editAccount() {
    FragmentManager fragmentManager = getParentFragmentManager();
    fragmentManager
        .beginTransaction()
        .replace(R.id.nav_host_fragment, MyAccountFragment.class, null)
        .setReorderingAllowed(true)
        .addToBackStack("MyAccountFragment") // name can be null
        .commit();
  }

//  // [START on_start_check_user]
//  @Override
//  public void onStart() {
//    super.onStart();
//    // Check if user is signed in (non-null) and update UI accordingly.
//    FirebaseUser currentUser = mAuth.getCurrentUser();
//    if (currentUser != null) {
//      reload();
//    }
//  }
//  // [END on_start_check_user]

  private void createAccount(String email, String password) {
    // [START create_user_with_email]
    mAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            getActivity(),
            task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
              }
            });
    // [END create_user_with_email]
  }

  private void signIn(String email, String password) {
    // [START sign_in_with_email]
    mAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            getActivity(),
            task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(getContext(), "Authentication success.", Toast.LENGTH_SHORT).show();
                updateUI(user);
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
              }
            });
    // [END sign_in_with_email]
  }

  private void sendEmailVerification() {
    // Send verification email
    // [START send_email_verification]
    final FirebaseUser user = mAuth.getCurrentUser();
    user.sendEmailVerification()
        .addOnCompleteListener(
            getActivity(),
            task -> {
              // Email sent
            });
    // [END send_email_verification]
  }

  private void reload() {}

  private void updateUI(FirebaseUser user) {
    authenticationService.setUser(user);
    fragmentService.navigateToLastFragment(getActivity());
  }
}

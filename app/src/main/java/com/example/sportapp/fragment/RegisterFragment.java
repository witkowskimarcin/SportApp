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

import com.example.sportapp.R;
import com.example.sportapp.model.User;
import com.example.sportapp.service.AuthenticationService;
import com.example.sportapp.service.FragmentService;
import com.example.sportapp.util.Utils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RegisterFragment extends Fragment {
  private static final String TAG = "RegisterFragment";
  private final AuthenticationService authenticationService = AuthenticationService.getInstance();
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  // [END declare_auth]
  private final FragmentService fragmentService = FragmentService.getInstance();
  Button buttonRegister;
  EditText editTextEmail;
  EditText editTextFirstname;
  EditText editTextLastname;
  EditText editTextAddress;
  EditText editTextCountry;
  EditText editTextCity;
  EditText editTextPostCode;
  EditText editTextPassword;
  EditText editTextPasswordConfirm;
  private FirebaseAuth mAuth;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_my_account, container, false);
    FirebaseApp.initializeApp(getContext());
    mAuth = FirebaseAuth.getInstance();
    buttonRegister = root.findViewById(R.id.buttonRegister);
    editTextEmail = root.findViewById(R.id.editTextEmail);
    editTextFirstname = root.findViewById(R.id.editTextFirstname);
    editTextLastname = root.findViewById(R.id.editTextLastname);
    editTextAddress = root.findViewById(R.id.editTextAddress);
    editTextCountry = root.findViewById(R.id.editTextCountry);
    editTextCity = root.findViewById(R.id.editTextCity);
    editTextPostCode = root.findViewById(R.id.editTextPostCode);
    editTextPassword = root.findViewById(R.id.editTextPassword);
    editTextPasswordConfirm = root.findViewById(R.id.editTextPasswordConfirm);

    buttonRegister.setOnClickListener(
        v -> {
          if (StringUtils.isBlank(editTextEmail.getText().toString())) {
            Toast.makeText(getContext(), "Email nieprawidłowy.", Toast.LENGTH_SHORT).show();
          } else if (StringUtils.isBlank(editTextPassword.getText().toString())) {
            Toast.makeText(getContext(), "Hasło nieprawidłowe.", Toast.LENGTH_SHORT).show();
          } else if (!editTextPassword
              .getText()
              .toString()
              .equals(editTextPasswordConfirm.getText().toString())) {
            Toast.makeText(getContext(), "Hasło nieprawidłowe.", Toast.LENGTH_SHORT).show();
          } else
            createAccount(
                editTextEmail.getText().toString(), editTextPassword.getText().toString());
        });

    return root;
  }

  @Override
  public void onStart() {
    super.onStart();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if (currentUser != null) {
      reload();
    }
  }

  private void createAccount(String email, String password) {
    if (StringUtils.isNotBlank(email)
        && StringUtils.isNotBlank(password)
        && password.length() > 5) {
      mAuth
          .createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(
              getActivity(),
              task -> {
                if (task.isSuccessful()) {
                  FirebaseUser user = mAuth.getCurrentUser();
                  updateUI(user);
                  setUserInfo(user);
                } else {
                  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                  Toast.makeText(
                          getContext(),
                          "Nie udało się stworzyć konta, spróbuj ponownie później.",
                          Toast.LENGTH_SHORT)
                      .show();
                  updateUI(null);
                }
              });
    } else {
      Toast.makeText(getContext(), "Adres e-mail lub hasło jest nieprawidłowe.", Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void setUserInfo(FirebaseUser user) {
    User userInfo = new User();
    userInfo.setEmail(user.getEmail());
    userInfo.setFirstname(editTextFirstname.getText().toString());
    userInfo.setLastname(editTextLastname.getText().toString());
    userInfo.setAddress(editTextAddress.getText().toString());
    userInfo.setCountry(editTextCountry.getText().toString());
    userInfo.setCity(editTextCity.getText().toString());
    userInfo.setPostcode(editTextPostCode.getText().toString());
    userInfo.setRole(User.Role.USER);

    if (StringUtils.isNotBlank(userInfo.getFirstname())
        && StringUtils.isNotBlank(userInfo.getLastname())) {

      Map<String, Object> request = Utils.objToMap(userInfo);
      request.remove("offers", null);

      db.collection("users")
          .document(user.getEmail())
          .set(request)
          .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
          .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
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
                Toast.makeText(getContext(), "Udało się zalogować!", Toast.LENGTH_SHORT).show();
                updateUI(user);
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(
                        getContext(),
                        "Logowanie nie powiodło się, błędne dane logowania.",
                        Toast.LENGTH_SHORT)
                    .show();
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

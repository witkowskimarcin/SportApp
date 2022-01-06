package com.example.sportapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import java.util.concurrent.atomic.AtomicReference;

public class MyAccountFragment extends Fragment {
  private static final String TAG = "EmailPassword";
  private final AuthenticationService authenticationService = AuthenticationService.getInstance();
  private FirebaseFirestore db = FirebaseFirestore.getInstance();

  // [END declare_auth]
  private final FragmentService fragmentService = FragmentService.getInstance();
  TextView textViewRegister;
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
    textViewRegister = root.findViewById(R.id.textViewRegister);
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

    if (authenticationService.isAuthenticated()) {
      User user = authenticationService.getUser();
      textViewRegister.setText("Edycja użytkownika");
      buttonRegister.setText("Zapisz");
      editTextEmail.setText(user.getEmail());
      editTextEmail.setEnabled(false);
      editTextFirstname.setText(user.getFirstname());
      editTextLastname.setText(user.getLastname());
      editTextAddress.setText(user.getAddress());
      editTextCountry.setText(user.getCountry());
      editTextCity.setText(user.getCity());
      editTextPostCode.setText(user.getPostcode());

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
            } else editAccount();
          });

      return root;
    }

    // wroc do logowania
    NavController navController =
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    navController.navigate(R.id.nav_login);
    return null;
  }

  private void editAccount() {
    AtomicReference<FirebaseUser> firebaseUser =
        new AtomicReference<>(authenticationService.getFirebaseUser());

    if (StringUtils.isNotBlank(editTextEmail.getText().toString())) {
      firebaseUser.get().updateEmail(editTextEmail.getText().toString());
    }

    if (StringUtils.isNotBlank(editTextPassword.getText().toString())
        && editTextPassword.getText().toString().length() > 5) {
      if (editTextPassword
          .getText()
          .toString()
          .equals(editTextPasswordConfirm.getText().toString())) {
        firebaseUser.get().updatePassword(editTextPassword.getText().toString());
      } else {
        Toast.makeText(getContext(), "Hasła nie są takie same.", Toast.LENGTH_SHORT).show();
      }
    }

    mAuth
        .updateCurrentUser(firebaseUser.get())
        .addOnCompleteListener(
            getActivity(),
            task -> {
              if (task.isSuccessful()) {
                firebaseUser.set(mAuth.getCurrentUser());
                updateUI(firebaseUser.get());
                setUserInfo(firebaseUser.get());
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
          .addOnSuccessListener(aVoid -> Log.d(TAG, "Dane użytkownika zostały zapisane."))
          .addOnFailureListener(e -> Log.w(TAG, "Błąd zapisu danych użytkownika.", e));
    }
  }

  private void updateUI(FirebaseUser user) {
    authenticationService.setUser(user);
    fragmentService.navigateToLastFragment(getActivity());
  }
}

package com.example.sportapp.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.sportapp.model.BoughtOffer;
import com.example.sportapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationService {
  FirebaseFirestore db = FirebaseFirestore.getInstance();

  private static final String TAG = "EmailPassword";

  private FirebaseAuth mAuth;

  private FirebaseUser user = null;

  private User userExtended = null;

  private boolean admin = false;

  private static AuthenticationService instance;

  public static AuthenticationService getInstance() {
    if (instance == null) instance = new AuthenticationService();

    return instance;
  }

  private AuthenticationService() {}

  public void setUser(FirebaseUser user) {
    this.user = user;
    this.checkIfIsAdmin();
    this.getUserInfo();
  }

  public User getUser() {
    return userExtended;
  }

  public static void setInstance(AuthenticationService instance) {
    AuthenticationService.instance = instance;
  }

  public boolean isAuthenticated() {
    return user != null;
  }

  public void logout() {
    user = null;
  }

  public void checkIfIsAdmin() {
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      DocumentReference docRef = db.collection("admins").document(user.getUid());
      docRef
          .get()
          .addOnCompleteListener(
              new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                      Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                      admin = true;
                    } else {
                      Log.d(TAG, "No such document");
                    }
                  } else {
                    Log.d(TAG, "get failed with ", task.getException());
                  }
                }
              });
    }
  }

  private void getUserInfo() {
    if (user != null) {
      DocumentReference docRef = db.collection("users").document(user.getEmail());
      docRef
          .get()
          .addOnCompleteListener(
              new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                      Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                      userExtended = document.toObject(User.class);
                      userExtended.setEmail(user.getEmail());
                      userExtended.setUuid(user.getUid());
                      getOffers();
                    } else {
                      Log.d(TAG, "No such document");
                    }
                  } else {
                    Log.d(TAG, "get failed with ", task.getException());
                  }
                }
              });
    }
  }

  private void getOffers() {
    if (userExtended != null) {
      Task<QuerySnapshot> querySnapshotTask =
          db.collection("users")
              .document(userExtended.getEmail())
              .collection("savedPositions")
              .get()
              .addOnCompleteListener(
                  new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                          List<BoughtOffer> _offers = new ArrayList<>();
                          List<DocumentSnapshot> documents = task.getResult().getDocuments();
                          for (DocumentSnapshot document : documents) {
                            BoughtOffer offer = document.toObject(BoughtOffer.class);
                            offer.setUuid(document.getId());
                            OffsetDateTime now = OffsetDateTime.now();
                            OffsetDateTime endDate =
                                OffsetDateTime.parse(
                                    offer.getEndDate(), DateTimeFormatter.ISO_DATE);
                            if (now.isAfter(endDate)) {
                              _offers.add(offer);
                            }
                          }
                          userExtended.setOffers(_offers);
                        }
                      } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                      }
                    }
                  });
    }
  }

  public void singIn(String email, String password, FragmentActivity activity, Context context) {
    mAuth = FirebaseAuth.getInstance();
    mAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            activity,
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithEmail:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  Toast.makeText(context, "Authentication success.", Toast.LENGTH_SHORT).show();
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "signInWithEmail:failure", task.getException());
                  Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
              }
            });
  }
}

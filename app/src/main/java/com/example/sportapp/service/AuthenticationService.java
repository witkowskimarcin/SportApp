package com.example.sportapp.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.example.sportapp.model.BoughtOffer;
import com.example.sportapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AuthenticationService {
  FirebaseFirestore db = FirebaseFirestore.getInstance();

  private static CustomObservable observable;

  private static final String TAG = "EmailPassword";

  private FirebaseAuth mAuth;

  private FirebaseUser user = null;

  private User userExtended = null;

  private boolean admin = false;

  private static AuthenticationService instance;

  public static AuthenticationService getInstance() {
    if (instance == null) {
      instance = new AuthenticationService();
      observable = new CustomObservable();
    }

    return instance;
  }

  private AuthenticationService() {}

  public void setUser(FirebaseUser user) {
    this.user = user;
    this.checkIfIsAdmin();
    this.fetchUserInfo();
  }

  public User getUser() {
    return userExtended;
  }

  public void addObserver(Observer observer) {
    observable.addObserver(observer);
  }

  public FirebaseUser getFirebaseUser() {
    return user;
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
              task -> {
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
              });
    }
  }

  public void fetchUserInfo() {
    if (user != null) {
      DocumentReference docRef = db.collection("users").document(user.getEmail());
      docRef
          .get()
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  DocumentSnapshot document = task.getResult();
                  if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    userExtended = document.toObject(User.class);
                    userExtended.setEmail(user.getEmail());
                    userExtended.setUuid(user.getUid());
                    fetchOffers();
                  } else {
                    Log.d(TAG, "No such document");
                  }
                } else {
                  Log.d(TAG, "get failed with ", task.getException());
                }
              });
    }
  }

  static class CustomObservable extends Observable {
    public void notifyChange() {
      setChanged();
      notifyObservers();
    }
  }

  public void fetchOffers() {
    if (userExtended != null) {
      Task<QuerySnapshot> querySnapshotTask =
          db.collection("users")
              .document(userExtended.getEmail())
              .collection("savedPositions")
              .get()
              .addOnCompleteListener(
                  task -> {
                    if (task.isSuccessful()) {
                      if (task.getResult() != null) {
                        List<BoughtOffer> _offers = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                          BoughtOffer offer = document.toObject(BoughtOffer.class);
                          offer.setUuid(document.getId());
                          OffsetDateTime now = OffsetDateTime.now();
                          DateTimeFormatter dateTimeFormatter =
                              DateTimeFormatter.ofPattern("yyyy-MM-dd");
                          LocalDate localDate =
                              LocalDate.from(dateTimeFormatter.parse(offer.getEndDate()));

                          OffsetDateTime endDate =
                              OffsetDateTime.of(
                                  localDate.getYear(),
                                  localDate.getMonthValue(),
                                  localDate.getDayOfMonth(),
                                  0,
                                  0,
                                  0,
                                  0,
                                  ZoneOffset.UTC);
                          if (now.isBefore(endDate)) {
                            _offers.add(offer);
                          }
                          userExtended.setOffers(_offers);
                          observable.notifyChange();
                          observable.notifyObservers();
                        }
                      }
                    } else {
                      Log.w(TAG, "Error getting documents.", task.getException());
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
            task -> {
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
            });
  }
}

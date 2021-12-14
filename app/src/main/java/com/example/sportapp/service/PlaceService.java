package com.example.sportapp.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sportapp.model.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceService {
    private static final String TAG = "Places";
    FirebaseFirestore db;

    private static PlaceService instance;

    public static PlaceService getInstance() {
        if (instance == null)
            instance = new PlaceService();

        return instance;
    }

    private PlaceService() {
        db = FirebaseFirestore.getInstance();
    }

    public List<Place> getPlacesByCity(String city) {
        Task<QuerySnapshot> querySnapshotTask = db.collection("places")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                List<Place> places = new ArrayList<>();
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for (DocumentSnapshot document : documents) {
                                    Place place = document.toObject(Place.class);
                                    place.setUuid(document.getId());
                                    places.add(place);
                                }
                                System.out.println("Do");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
//        List<Place> places = querySnapshotTask.getResult().toObjects(Place.class);
        return null;
    }
}

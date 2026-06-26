package com.example.datsanbong.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRealtimeService {
    private static final FirebaseDatabase database =
            FirebaseDatabase.getInstance();

    public static DatabaseReference getReference(String node){
        return database.getReference(node);
    }

}

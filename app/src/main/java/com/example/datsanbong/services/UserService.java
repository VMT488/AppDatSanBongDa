package com.example.datsanbong.services;

import com.example.datsanbong.models.User;
import com.google.firebase.database.DatabaseReference;

public class UserService {

    private final DatabaseReference reference =
            FirebaseRealtimeService.getReference("Users");

    public void saveUser(User user){

        reference.child(user.getUid())
                .setValue(user);

    }

}
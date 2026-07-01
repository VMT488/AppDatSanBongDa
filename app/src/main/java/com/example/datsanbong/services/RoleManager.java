package com.example.datsanbong.services;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class RoleManager {

    public interface RoleCallback {
        void onResult(boolean isAdmin);
    }

    public void checkAdmin(RoleCallback callback) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            callback.onResult(false);
            return;
        }

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String role = snapshot.getValue(String.class);

                        callback.onResult("ADMIN".equals(role));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        callback.onResult(false);

                    }
                });
    }

}
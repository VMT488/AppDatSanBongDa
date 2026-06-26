package com.example.datsanbong.services;

import com.example.datsanbong.listeners.FirebaseCallback;
import com.example.datsanbong.models.SanBong;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SanBongService {
    private final DatabaseReference reference =
            FirebaseRealtimeService.getReference("DanhSachSanBong");

    public void addSanBong(SanBong sanBong){

        reference.child(String.valueOf(sanBong.getId()))
                .setValue(sanBong);

    }

    public void getAll(FirebaseCallback<SanBong> callback){

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<SanBong> list = new ArrayList<>();

                for(DataSnapshot data : snapshot.getChildren()){

                    SanBong san =
                            data.getValue(SanBong.class);

                    if(san != null){
                        list.add(san);
                    }

                }

                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                callback.onFailure(error.getMessage());

            }
        });

    }


}

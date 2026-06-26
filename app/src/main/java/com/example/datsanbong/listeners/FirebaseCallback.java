package com.example.datsanbong.listeners;

import java.util.List;

public interface FirebaseCallback<T> {
    void onSuccess(List<T> list);

    void onFailure(String message);
}

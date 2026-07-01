package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datsanbong.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser firebaseUser = auth.getCurrentUser();

        // Chưa đăng nhập
        if (firebaseUser == null) {
            openLogin();
            return;
        }

        // Đã đăng nhập -> kiểm tra trạng thái trên Realtime Database
        checkUser(firebaseUser.getUid());
    }

    private void checkUser(String uid) {

        userRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            auth.signOut();
                            openLogin();
                            return;
                        }

                        User user = snapshot.getValue(User.class);

                        if (user == null) {

                            auth.signOut();
                            openLogin();
                            return;
                        }

                        // Kiểm tra tài khoản bị khóa
                        if (!user.isActive()) {

                            Toast.makeText(
                                    SplashActivity.this,
                                    "Tài khoản đã bị khóa",
                                    Toast.LENGTH_SHORT
                            ).show();

                            auth.signOut();
                            openLogin();
                            return;
                        }

                        // Điều hướng theo role
                        startActivity(new Intent(
                                SplashActivity.this,
                                MainActivity.class));

                        finish();

                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Toast.makeText(
                                SplashActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        openLogin();
                    }
                });
    }

    private void openLogin() {

        startActivity(new Intent(
                SplashActivity.this,
                LoginActivity.class));

        finish();
    }
}
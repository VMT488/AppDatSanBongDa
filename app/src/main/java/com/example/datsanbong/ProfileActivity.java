package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datsanbong.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProfileActivity extends BaseActivity {

    private TextView txtName;
    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtRole;
    private TextView txtStatus;

    private Button btnLogout;
    private Button btnEdit;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupToolbar("Thông tin tài khoản");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông tin tài khoản");
        }

        txtName = findViewById(R.id.txtName);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);

        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEdit);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        loadUser();

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

        });

        btnEdit.setOnClickListener(v -> {

            // Làm sau
        });

    }

    private void loadUser() {

        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);

                        if(user == null) return;

                        txtName.setText(user.getName());

                        txtUsername.setText(user.getUsername());

                        txtEmail.setText(user.getEmail());

                        txtPhone.setText(user.getPhone());

                        txtRole.setText(user.getRole());

                        txtStatus.setText(
                                user.isActive()
                                        ? "Hoạt động"
                                        : "Đã khóa"
                        );

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }

                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
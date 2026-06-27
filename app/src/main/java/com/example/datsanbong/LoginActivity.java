package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datsanbong.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    private Button btnLogin;

    private TextView txtRegister;

    private FirebaseAuth auth;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtRegister = findViewById(R.id.txtRegister);

        txtRegister.setOnClickListener(v -> {

            startActivity(new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            ));

        });

        btnLogin.setOnClickListener(v -> login());

    }

    private void login() {

        String usernameOrEmail =
                edtUsername.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        if (usernameOrEmail.isEmpty()) {
            edtUsername.setError("Nhập email hoặc username");
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Nhập mật khẩu");
            return;
        }

        // Nếu là email
        if (usernameOrEmail.contains("@")) {

            loginWithEmail(usernameOrEmail, password);

        } else {

            loginWithUsername(usernameOrEmail, password);

        }

    }

    private void loginWithEmail(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        loadUser(auth.getCurrentUser().getUid());

                    } else {

                        Toast.makeText(
                                this,
                                "Sai email hoặc mật khẩu",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }

    private void loginWithUsername(String username,
                                   String password) {

        userRef.orderByChild("username")
                .equalTo(username)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Tên đăng nhập không tồn tại",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    for (DataSnapshot data : snapshot.getChildren()) {

                        User user = data.getValue(User.class);

                        if (user != null) {

                            loginWithEmail(user.getEmail(), password);
                            return;

                        }

                    }

                });

    }

    private void loadUser(String uid) {

        userRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Không tìm thấy thông tin người dùng",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        User user = snapshot.getValue(User.class);

                        if (user == null) return;

                        if (!user.isActive()) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Tài khoản đã bị khóa",
                                    Toast.LENGTH_SHORT
                            ).show();

                            auth.signOut();

                            return;
                        }

                        if ("ADMIN".equals(user.getRole())) {

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            AdminActivity.class
                                    )
                            );

                        } else {

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            MainActivity.class
                                    )
                            );

                        }

                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Toast.makeText(
                                LoginActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {

            loadUser(user.getUid());

        }
    }

}
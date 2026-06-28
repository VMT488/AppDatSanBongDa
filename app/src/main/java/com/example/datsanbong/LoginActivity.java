package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datsanbong.models.User;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private Button btnGoogle;
    private TextView txtRegister;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> googleLauncher;
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

        btnGoogle = findViewById(R.id.btnGoogle);

        btnGoogle.setOnClickListener(v->{

            Intent signInIntent =
                    googleSignInClient.getSignInIntent();

            googleLauncher.launch(signInIntent);

        });

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(R.string.default_web_client_id)
                        )
                        .requestEmail()
                        .build();

        googleSignInClient =
                GoogleSignIn.getClient(this, gso);

        googleLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if(result.getResultCode()!=RESULT_OK)
                                return;

                            Intent data = result.getData();

                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(data);

                            try{

                                GoogleSignInAccount account =
                                        task.getResult(ApiException.class);

                                firebaseAuthWithGoogle(
                                        account.getIdToken()
                                );

                            }catch (Exception e){

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();

                            }

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

    private void firebaseAuthWithGoogle(String idToken){

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if(!task.isSuccessful()){

                        Toast.makeText(
                                this,
                                "Đăng nhập thất bại",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    FirebaseUser firebaseUser =
                            auth.getCurrentUser();

                    createGoogleUser(firebaseUser);

                });

    }

    private void createGoogleUser(FirebaseUser firebaseUser){

        String uid = firebaseUser.getUid();

        userRef.child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                if(snapshot.exists()){

                                    loadUser(uid);
                                    return;

                                }

                                User user = new User();

                                user.setUid(uid);

                                user.setName(
                                        firebaseUser.getDisplayName()
                                );

                                user.setUsername(
                                        firebaseUser.getEmail()
                                                .split("@")[0]
                                );

                                user.setEmail(
                                        firebaseUser.getEmail()
                                );

                                user.setPhone("");

                                user.setActive(true);

                                user.setRole("USER");

                                userRef.child(uid)
                                        .setValue(user)
                                        .addOnSuccessListener(unused -> {

                                            loadUser(uid);

                                        });

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

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
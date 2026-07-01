package com.example.datsanbong;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    private EditText edtName, edtUsername, edtPhone;
    private Button btnSave;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupToolbar("Chỉnh sửa thông tin");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edtName = findViewById(R.id.edtEditName);
        edtUsername = findViewById(R.id.edtEditUsername);
        edtPhone = findViewById(R.id.edtEditPhone);
        btnSave = findViewById(R.id.btnSaveProfile);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            edtName.setText(bundle.getString("oldName"));
            edtUsername.setText(bundle.getString("oldUsername"));
            edtPhone.setText(bundle.getString("oldPhone"));
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        btnSave.setOnClickListener(v -> LooThongTinMoi());
    }

    private void LooThongTinMoi() {
        String name = edtName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng không để trống thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("username", username);
        updates.put("phone", phone);

        userRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
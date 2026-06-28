package com.example.datsanbong;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.UserAdapter;
import com.example.datsanbong.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminCustomerActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        setContentView(R.layout.activity_admin_customer);

        Toolbar toolbar = findViewById(R.id.toolbarCustomer);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCustomers = findViewById(R.id.rvCustomers);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        rvCustomers.setAdapter(userAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        loadUserData();
    }

    private void loadUserData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Object rawData = dataSnapshot.getValue();
                    if (!(rawData instanceof Map)) {
                        continue;
                    }

                    Map<String, Object> userMap = (Map<String, Object>) rawData;

                    String uid = dataSnapshot.getKey();

                    String name = userMap.containsKey("name") ? String.valueOf(userMap.get("name")) : "";
                    String username = userMap.containsKey("username") ? String.valueOf(userMap.get("username")) : "";
                    String email = userMap.containsKey("email") ? String.valueOf(userMap.get("email")) : "";
                    String role = userMap.containsKey("role") ? String.valueOf(userMap.get("role")) : "";

                    String phone = "";
                    if (userMap.containsKey("phone") && userMap.get("phone") != null) {
                        phone = String.valueOf(userMap.get("phone"));
                    }

                    boolean isActive = false;
                    if (userMap.containsKey("active") && userMap.get("active") != null) {
                        Object activeValue = userMap.get("active");
                        if (activeValue instanceof Boolean) {
                            isActive = (Boolean) activeValue;
                        } else {
                            isActive = "true".equalsIgnoreCase(String.valueOf(activeValue));
                        }
                    }

                    if ("USER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
                        User user = new User(uid, name, username, email, phone, isActive, role);
                        userList.add(user);
                    }
                }

                // Cập nhật lên giao diện RecyclerView
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminCustomerActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
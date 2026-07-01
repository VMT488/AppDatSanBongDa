package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.UserAdapter;
import com.example.datsanbong.models.User;
import com.example.datsanbong.services.RoleManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer);
        RoleManager roleManager = new RoleManager();

        roleManager.checkAdmin(admin -> {

            if (!admin) {
                Toast.makeText(
                        this,
                        "Bạn không có quyền truy cập",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }

        });
        Toolbar toolbar = findViewById(R.id.toolbarCustomer);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else if (id == R.id.nav_quan_ly_san) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminCustomerActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_thong_ke) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminCustomerActivity.this, AdminRevenueActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_manage_customers) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        rvCustomers = findViewById(R.id.rvCustomers);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        rvCustomers.setAdapter(userAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        loadUserData();
    }
    public void thayDoiTrangThaiBlockUser(User user) {
        boolean trangThaiMoi = !user.isActive();
        String hanhDong = trangThaiMoi ? "Mở khóa (Active)" : "Khóa (Block)";

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận thay đổi")
                .setMessage("Bạn có chắc chắn muốn " + hanhDong + " tài khoản: " + user.getName() + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {

                    mDatabase.child(user.getUid()).child("active").setValue(trangThaiMoi)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(AdminCustomerActivity.this, "Đã " + hanhDong + " thành công!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AdminCustomerActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
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
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminCustomerActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
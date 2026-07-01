package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.SanBongAdapter;
import com.example.datsanbong.models.SanBong;
import com.example.datsanbong.services.RoleManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSanBong;
    private SanBongAdapter sanBongAdapter;
    private List<SanBong> mListSanBong;
    private EditText edtSearch;
    private DatabaseReference db;
    private boolean isAdmin = false;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("DanhSachSanBong");
        rvSanBong = findViewById(R.id.rvSanBong);
        edtSearch = findViewById(R.id.edtSearch);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        checkingRole();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSanBong.setLayoutManager(linearLayoutManager);

        mListSanBong = new ArrayList<>();
        sanBongAdapter = new SanBongAdapter(mListSanBong);
        rvSanBong.setAdapter(sanBongAdapter);

        listenFirebase();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sanBongAdapter != null) {
                    sanBongAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_admin) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                return true;
            }

            if(id == R.id.nav_profile){

                startActivity(new Intent(
                        MainActivity.this,
                        ProfileActivity.class));

                return true;
            }

            if(id == R.id.nav_admin){

                startActivity(new Intent(
                        MainActivity.this,
                        AdminActivity.class));

                return true;
            }

            return false;

        });

        sanBongAdapter.setOnItemClickListener(sanBong -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);

            intent.putExtra("tenSan", sanBong.getTenSan());
            intent.putExtra("diaChi", sanBong.getDiaChi());
            intent.putExtra("giaSan", sanBong.getGiaSan());
            intent.putExtra("hinhAnh", sanBong.getHinhAnh());
            intent.putExtra("documentId", sanBong.getRealtimeKey());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void listenFirebase() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSanBong.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SanBong sanBong = dataSnapshot.getValue(SanBong.class);
                    if (sanBong != null) {
                        sanBong.setRealtimeKey(dataSnapshot.getKey());
                        mListSanBong.add(sanBong);
                    }
                }
                if (sanBongAdapter != null) {
                    sanBongAdapter.setDanhSachGoc(new ArrayList<>(mListSanBong));
                    sanBongAdapter.notifyDataSetChanged();
                    String chuDangTimKiem = edtSearch.getText().toString();
                    if (!chuDangTimKiem.isEmpty()) {
                        sanBongAdapter.filter(chuDangTimKiem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkingRole() {

        RoleManager roleManager = new RoleManager();

        roleManager.checkAdmin(admin -> {

            runOnUiThread(() -> {

                Menu menu = bottomNavigationView.getMenu();

                menu.findItem(R.id.nav_admin)
                        .setVisible(admin);

            });

        });

    }
}
package com.example.datsanbong;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.SanBongAdapter;
import com.example.datsanbong.models.SanBong;
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
    private BottomNavigationView bottomNavigationView;
    private Button btnFilterAll, btnFilterSan5, btnFilterSan7, btnFilterSan11;
    private String loaiSanDangChon = "Tất cả";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("DanhSachSanBong");
        rvSanBong = findViewById(R.id.rvSanBong);
        edtSearch = findViewById(R.id.edtSearch);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterSan5 = findViewById(R.id.btnFilterSan5);
        btnFilterSan7 = findViewById(R.id.btnFilterSan7);
        btnFilterSan11 = findViewById(R.id.btnFilterSan11);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSanBong.setLayoutManager(linearLayoutManager);

        mListSanBong = new ArrayList<>();
        sanBongAdapter = new SanBongAdapter(mListSanBong);
        rvSanBong.setAdapter(sanBongAdapter);

        listenFirebase();

        btnFilterAll.setOnClickListener(v -> updateFilterSelection("Tất cả", btnFilterAll));
        btnFilterSan5.setOnClickListener(v -> updateFilterSelection("5", btnFilterSan5)); // "5" sẽ quét trúng "Sân 5", "Sân 5 người"
        btnFilterSan7.setOnClickListener(v -> updateFilterSelection("7", btnFilterSan7));
        btnFilterSan11.setOnClickListener(v -> updateFilterSelection("11", btnFilterSan11));
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
            } else if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }else if (id == R.id.nav_booking) {
                Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
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
    private void updateFilterSelection(String loaiSan, Button buttonDuocChon) {
        this.loaiSanDangChon = loaiSan;

        btnFilterAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
        btnFilterSan5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
        btnFilterSan7.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
        btnFilterSan11.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));

        buttonDuocChon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#007A33")));

        if (sanBongAdapter != null) {
            sanBongAdapter.filter(edtSearch.getText().toString(), loaiSanDangChon);
        }
    }
}
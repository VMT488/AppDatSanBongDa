package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.SanBongAdapter;
import com.example.datsanbong.models.KhungGio;
import com.example.datsanbong.models.SanBong;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DrawerLayout drawerLayout;
    private RecyclerView rvAdminSanBong;
    private SanBongAdapter sanBongAdapter;
    private List<SanBong> mListSanBong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://datsanbong-b6ad1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .child("DanhSachSanBong");

        Button btnThemSan = findViewById(R.id.btnThemSan);
        Toolbar toolbarAdmin = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbarAdmin);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        rvAdminSanBong = findViewById(R.id.rvAdminSanBong);
        if (rvAdminSanBong != null) {
            rvAdminSanBong.setLayoutManager(new LinearLayoutManager(this));
            mListSanBong = new ArrayList<>();
            sanBongAdapter = new SanBongAdapter(mListSanBong);
            rvAdminSanBong.setAdapter(sanBongAdapter);

            sanBongAdapter.setOnItemClickListener(sanBong -> {
                Intent intent = new Intent(AdminActivity.this, DetailActivity.class);
                intent.putExtra("tenSan", sanBong.getTenSan());
                intent.putExtra("diaChi", sanBong.getDiaChi());
                intent.putExtra("giaSan", sanBong.getGiaSan());
                intent.putExtra("hinhAnh", sanBong.getHinhAnh());
                intent.putExtra("documentId", sanBong.getRealtimeKey());
                startActivity(intent);
            });
        }

        listenFirebaseForAdmin();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarAdmin, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                } else if (id == R.id.nav_thong_ke) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminActivity.this, AdminRevenueActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_manage_customers) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminActivity.this, AdminCustomerActivity.class);
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        if (btnThemSan != null) {
            btnThemSan.setOnClickListener(v -> showDialogThemSan());
        }
    }

    private void listenFirebaseForAdmin() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListSanBong == null || sanBongAdapter == null) return;

                mListSanBong.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SanBong sanBong = dataSnapshot.getValue(SanBong.class);
                    if (sanBong != null) {
                        sanBong.setRealtimeKey(dataSnapshot.getKey());
                        mListSanBong.add(sanBong);
                    }
                }
                sanBongAdapter.setDanhSachGoc(new ArrayList<>(mListSanBong));
                sanBongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Lỗi cập nhật danh sách quản lý: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogThemSan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Thêm Sân Bóng Mới");

        View view = LayoutInflater.from(AdminActivity.this).inflate(R.layout.dialog_them_san, null);
        final EditText edtTen = view.findViewById(R.id.edtTenSan);
        final EditText edtLoaiSan = view.findViewById(R.id.edtLoaiSan); // Bổ sung ánh xạ loại sân
        final EditText edtDiaChi = view.findViewById(R.id.edtDiaChi);
        final EditText edtGia = view.findViewById(R.id.edtGiaSan);
        final EditText edtLinkAnh = view.findViewById(R.id.edtLinkAnh);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String ten = edtTen.getText().toString().trim();
            String loaiSan = edtLoaiSan.getText().toString().trim(); // Lấy dữ liệu loại sân
            String diaChi = edtDiaChi.getText().toString().trim();
            String gia = edtGia.getText().toString().trim();
            String linkAnh = edtLinkAnh.getText().toString().trim();

            Toast.makeText(AdminActivity.this, "Đang xử lý thông tin sân...", Toast.LENGTH_SHORT).show();

            // Bổ sung điều kiện kiểm tra không được để trống loại sân
            if (!ten.isEmpty() && !loaiSan.isEmpty() && !diaChi.isEmpty() && !gia.isEmpty()) {
                try {
                    int idNgauNhien = (int) (System.currentTimeMillis() / 1000);
                    String anhSif;
                    if (linkAnh.isEmpty()) {
                        anhSif = "san5";
                    } else if (linkAnh.startsWith("http://") || linkAnh.startsWith("https://")) {
                        anhSif = linkAnh;
                    } else {
                        String tenAnhTinhChinh = linkAnh;
                        if (tenAnhTinhChinh.contains(".")) {
                            tenAnhTinhChinh = tenAnhTinhChinh.substring(0, tenAnhTinhChinh.lastIndexOf("."));
                        }
                        anhSif = tenAnhTinhChinh;
                    }
                    String giaSach = gia.replaceAll("[^0-9]", "");
                    long giaSan = Long.parseLong(giaSach);

                    List<KhungGio> danhSachGioTrong = taoDanhSachCaMacDinhForAdmin();

                    SanBong sanBongMoi = new SanBong(idNgauNhien, ten, diaChi, giaSan, anhSif, loaiSan, danhSachGioTrong);

                    String customKey = mDatabase.push().getKey();

                    if (customKey != null) {
                        mDatabase.child(customKey).setValue(sanBongMoi)
                                .addOnSuccessListener(unused -> Toast.makeText(AdminActivity.this, "THÀNH CÔNG: Đã xuất hiện trên Realtime!", Toast.LENGTH_LONG).show())
                                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "LỖI FIREBASE: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    } else {
                        Toast.makeText(AdminActivity.this, "Lỗi: Không tạo được mã Key cho sân!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AdminActivity.this, "Lỗi định dạng: Giá sân phải nhập số nguyên thuần túy!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AdminActivity.this, "Thất bại: Bạn để trống Tên, Loại sân, Địa chỉ hoặc Giá tiền!", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private List<KhungGio> taoDanhSachCaMacDinhForAdmin() {
        List<KhungGio> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.set(Calendar.HOUR_OF_DAY, 16); long c1Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 17); cal.set(Calendar.MINUTE, 30); long c1End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_1", c1Start, c1End, false));

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 17); cal.set(Calendar.MINUTE, 30); long c2Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 19); cal.set(Calendar.MINUTE, 0); long c2End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_2", c2Start, c2End, false));

        cal.set(Calendar.HOUR_OF_DAY, 19); cal.set(Calendar.MINUTE, 0); long c3Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 20); cal.set(Calendar.MINUTE, 30); long c3End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_3", c3Start, c3End, false));

        cal.set(Calendar.HOUR_OF_DAY, 20); cal.set(Calendar.MINUTE, 30); long c4Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 22); cal.set(Calendar.MINUTE, 0); long c4End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_4", c4Start, c4End, false));

        return list;
    }
}
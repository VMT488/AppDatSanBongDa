package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.datsanbong.models.KhungGio;
import com.example.datsanbong.models.SanBong;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance().getReference("DanhSachSanBong");

        Button btnThemSan = findViewById(R.id.btnThemSan);
        Toolbar toolbarAdmin = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbarAdmin);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarAdmin, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_quan_ly_san) {
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

    private void showDialogThemSan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Thêm Sân Bóng Mới");

        View view = LayoutInflater.from(AdminActivity.this).inflate(R.layout.dialog_them_san, null);
        final EditText edtTen = view.findViewById(R.id.edtTenSan);
        final EditText edtDiaChi = view.findViewById(R.id.edtDiaChi);
        final EditText edtGia = view.findViewById(R.id.edtGiaSan);
        final EditText edtLinkAnh = view.findViewById(R.id.edtLinkAnh);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String ten = edtTen.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String gia = edtGia.getText().toString().trim();
            String linkAnh = edtLinkAnh.getText().toString().trim();

            if (!ten.isEmpty() && !diaChi.isEmpty() && !gia.isEmpty()) {
                int idNgauNhien = (int) (System.currentTimeMillis() / 1000);
                String anhSif = linkAnh.isEmpty() ? "https://vietnamisawesome.com/wp-content/uploads/2023/10/san-bong.jpg" : linkAnh;
                long giaSan = Long.parseLong(gia);

                List<KhungGio> danhSachGioTrong = new ArrayList<>();
                SanBong sanBongMoi = new SanBong(idNgauNhien, ten, diaChi, giaSan, anhSif, danhSachGioTrong);

                String customKey = mDatabase.push().getKey();

                if (customKey != null) {
                    mDatabase.child(customKey).setValue(sanBongMoi)
                            .addOnSuccessListener(unused -> Toast.makeText(AdminActivity.this, "Đã thêm sân lên Realtime DB thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Lỗi Realtime: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(AdminActivity.this, "Vui lòng điền đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        Button btnThemSan = findViewById(R.id.btnThemSan);

        // 1. Cấu hình Toolbar phía trên làm ActionBar
        Toolbar toolbarAdmin = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbarAdmin);

        // 2. Ánh xạ DrawerLayout điều khiển Menu trượt
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // 3. Tạo nút Hamburger (3 gạch) và đồng bộ với trạng thái đóng/mở Menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarAdmin, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Lắng nghe sự kiện click mở Dialog thêm sân bóng
        if (btnThemSan != null) {
            btnThemSan.setOnClickListener(v -> showDialogThemSan());
        }

        // 5. Cấu hình chuyển hướng cho các thành phần trong Custom Menu
        View menuRevenue = findViewById(R.id.menuRevenue);
        View menuSanBong = findViewById(R.id.menuSanBong);
        View menuCustomer = findViewById(R.id.menuCustomer);

        if (menuSanBong != null) {
            menuSanBong.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        }

        if (menuRevenue != null) {
            menuRevenue.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(AdminActivity.this, AdminRevenueActivity.class);
                startActivity(intent);
            });
        }

        if (menuCustomer != null) {
            menuCustomer.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(AdminActivity.this, AdminCustomerActivity.class);
                startActivity(intent);
            });
        }
    }

    // Hiển thị Dialog thêm sân bóng mới và tạo danh sách KhungGio rỗng ban đầu cho Firebase
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

                // Khởi tạo danh sách KhungGio trống ban đầu để đẩy lên cấu trúc Firebase mới
                List<KhungGio> danhSachGioTrong = new ArrayList<>();

                // Đóng gói đối tượng SanBong mới chứa List<KhungGio>
                SanBong sanBongMoi = new SanBong(idNgauNhien, ten, diaChi, giaSan, anhSif, danhSachGioTrong);

                db.collection("DanhSachSanBong")
                        .add(sanBongMoi)
                        .addOnSuccessListener(documentReference -> Toast.makeText(AdminActivity.this, "Đã thêm sân lên Firebase thành công!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(AdminActivity.this, "Vui lòng điền đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
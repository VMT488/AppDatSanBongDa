package com.example.datsanbong;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.adapters.AdminSanBongAdapter;
import com.example.datsanbong.models.SanBong;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<SanBong> listSanBong;
    private List<String> listDocumentIds;
    private AdminSanBongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        RecyclerView rvAdminSanBong = findViewById(R.id.rvAdminSanBong);
        Button btnThemSan = findViewById(R.id.btnThemSan);

        listSanBong = new ArrayList<>();
        listDocumentIds = new ArrayList<>();

        rvAdminSanBong.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter nhận sự kiện xóa sân từ giao diện quản lý
        adapter = new AdminSanBongAdapter(listSanBong, listDocumentIds, (documentId, position) -> xoaSanBongFirestore(documentId));
        rvAdminSanBong.setAdapter(adapter);

        // Bắt đầu lắng nghe thay đổi dữ liệu thời gian thực từ Firestore
        langNgheDuLieuAdmin();

        // Xử lý sự kiện khi ấn nút thêm sân mới
        btnThemSan.setOnClickListener(v -> showDialogThemSan());
    }


    private void langNgheDuLieuAdmin() {
        db.collection("DanhSachSanBong")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        listSanBong.clear();
                        listDocumentIds.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            SanBong sanBong = doc.toObject(SanBong.class);
                            listSanBong.add(sanBong);
                            listDocumentIds.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showDialogThemSan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Sân Bóng Mới");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_san, null);
        final EditText edtTen = view.findViewById(R.id.edtTenSan);
        final EditText edtDiaChi = view.findViewById(R.id.edtDiaChi);
        final EditText edtGia = view.findViewById(R.id.edtGiaSan);
        final EditText edtKhungGio = view.findViewById(R.id.edtKhungGio);
        final EditText edtLinkAnh = view.findViewById(R.id.edtLinkAnh);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String ten = edtTen.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String gia = edtGia.getText().toString().trim();
            String khungGio = edtKhungGio.getText().toString().trim();
            String linkAnh = edtLinkAnh.getText().toString().trim();

            if (!ten.isEmpty() && !diaChi.isEmpty() && !gia.isEmpty() && !khungGio.isEmpty()) {
                int idNgauNhien = (int) (System.currentTimeMillis() / 1000);
                String anhSif = linkAnh.isEmpty() ? "https://vietnamisawesome.com/wp-content/uploads/2023/10/san-bong.jpg" : linkAnh;
                long giaSan = Long.parseLong(gia);
                SanBong sanBongMoi = new SanBong(idNgauNhien, ten, diaChi, giaSan, anhSif, khungGio);

                db.collection("DanhSachSanBong")
                        .add(sanBongMoi)
                        .addOnSuccessListener(documentReference -> Toast.makeText(AdminActivity.this, "Đã thêm sân lên Firebase thành công!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(AdminActivity.this, "Vui lòng điền đủ thông tin bao gồm cả khung giờ!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void xoaSanBongFirestore(String documentId) {
        db.collection("DanhSachSanBong").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa sân khỏi Firebase!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show());
    }
}
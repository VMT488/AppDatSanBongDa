package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.adapters.SanBongAdapter;
import com.example.datsanbong.models.SanBong;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSanBong;
    private SanBongAdapter sanBongAdapter;
    private List<SanBong> mListSanBong;
    private EditText edtSearch;
    // Khai báo biến Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        rvSanBong = findViewById(R.id.rvSanBong);
        edtSearch = findViewById(R.id.edtSearch); // Ánh xạ ô tìm kiếm trong layout của bạn

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSanBong.setLayoutManager(linearLayoutManager);

        mListSanBong = new ArrayList<>();
        sanBongAdapter = new SanBongAdapter(mListSanBong);
        rvSanBong.setAdapter(sanBongAdapter);

        // Gọi hàm lắng nghe dữ liệu trực tuyến từ Firebase đám mây
        langNgheDuLieuFirebase();

        //
        edtSearch.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            return true;
        });
    }

    // Hàm lấy dữ liệu thời gian thực từ Cloud Firestore
    private void langNgheDuLieuFirebase() {
        db.collection("DanhSachSanBong")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        mListSanBong.clear(); // Xóa danh sách cũ tránh trùng lặp dữ liệu
                        for (QueryDocumentSnapshot doc : value) {
                            // Tự động ép kiểu dữ liệu Firebase thành Object Java SanBong
                            SanBong sanBong = doc.toObject(SanBong.class);
                            mListSanBong.add(sanBong);
                        }
                        // Làm mới danh sách hiển thị trên màn hình User
                        sanBongAdapter.notifyDataSetChanged();
                    }
                });
    }
}
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
    // Khai báo biến Firestore
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance()
                .getReference("SanBong");
        rvSanBong = findViewById(R.id.rvSanBong);
        edtSearch = findViewById(R.id.edtSearch); // Ánh xạ ô tìm kiếm trong layout của bạn

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSanBong.setLayoutManager(linearLayoutManager);

        mListSanBong = new ArrayList<>();
        sanBongAdapter = new SanBongAdapter(mListSanBong);
        rvSanBong.setAdapter(sanBongAdapter);

        // Gọi hàm lắng nghe dữ liệu trực tuyến từ Firebase đám mây
        listenFirebase();

        //
        edtSearch.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            return true;
        });

        sanBongAdapter.setOnItemClickListener(sanBong -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            DetailActivity.class
                    );

            intent.putExtra("tenSan", sanBong.getTenSan());
            intent.putExtra("diaChi", sanBong.getDiaChi());
            intent.putExtra("giaSan", sanBong.getGiaSan());
            intent.putExtra("hinhAnh", sanBong.getHinhAnh());

            startActivity(intent);
        });

    }

    // Hàm lấy dữ liệu thời gian thực từ Cloud Firestore
    private void listenFirebase() {

        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                mListSanBong.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    SanBong sanBong = dataSnapshot.getValue(SanBong.class);

                    if (sanBong != null) {
                        mListSanBong.add(sanBong);
                    }

                }

                sanBongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(
                        MainActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

    }
}
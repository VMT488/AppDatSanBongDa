package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.R;
import com.example.datsanbong.adapters.SanBongAdapter;
import com.example.datsanbong.models.SanBong;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSanBong;
    private SanBongAdapter sanBongAdapter;
    private List<SanBong> mListSanBong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvSanBong = findViewById(R.id.rvSanBong);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSanBong.setLayoutManager(linearLayoutManager);

        mListSanBong = getMockDataSanBong();

        sanBongAdapter = new SanBongAdapter(mListSanBong);
        rvSanBong.setAdapter(sanBongAdapter);

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

    private List<SanBong> getMockDataSanBong() {
        List<SanBong> list = new ArrayList<>();

        list.add(new SanBong(1, "Sân bóng Đại học Bách Khoa", "Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội", "300.000đ/trận", R.drawable.san11));
        list.add(new SanBong(2, "Sân bóng Thượng Đình", "129 Nguyễn Trãi, Thanh Xuân, Hà Nội", "250.000đ/trận", R.drawable.san5));
        list.add(new SanBong(3, "Sân bóng đá Mini Thành Phát", "Số 2 Hoàng Minh Giám, Cầu Giấy, Hà Nội", "400.000đ/trận", R.drawable.san7));
        list.add(new SanBong(4, "Sân bóng Việt Hùng", "Đông Anh, Hà Nội", "200.000đ/trận", R.drawable.san5));

        return list;
    }
}
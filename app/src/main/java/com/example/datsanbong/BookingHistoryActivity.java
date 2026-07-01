package com.example.datsanbong;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.adapters.BookingHistoryAdapter;
import com.example.datsanbong.models.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingHistoryActivity extends BaseActivity {

    private RecyclerView rvBookingHistory;
    private BookingHistoryAdapter adapter;
    private List<Booking> mListBooking;
    private DatabaseReference mDatabaseBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        setupToolbar("Lịch sử đặt sân");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));

        mListBooking = new ArrayList<>();
        adapter = new BookingHistoryAdapter(mListBooking);
        rvBookingHistory.setAdapter(adapter);

        // Khởi tạo tham chiếu Firebase đến node "Bookings"
        mDatabaseBookings = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://datsanbong-b6ad1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .child("Bookings");

        loadLichSuDatSan();
    }

    private void loadLichSuDatSan() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Sử dụng query lọc dữ liệu theo trường "userId" bằng equalTo
        mDatabaseBookings.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListBooking.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Booking booking = data.getValue(Booking.class);
                            if (booking != null) {
                                mListBooking.add(booking);
                            }
                        }

                        // Sắp xếp lịch sử: đưa các đơn đặt sân mới nhất lên trên cùng
                        Collections.sort(mListBooking, (b1, b2) -> Long.compare(b2.getCreatedAt(), b1.getCreatedAt()));

                        adapter.notifyDataSetChanged();
                        if (mListBooking.isEmpty()) {
                            Toast.makeText(BookingHistoryActivity.this, "Bạn chưa có lịch sử đặt sân nào!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingHistoryActivity.this, "Lỗi tải lịch sử: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
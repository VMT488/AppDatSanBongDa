package com.example.datsanbong;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Các thư viện Android Core và Giao diện hệ thống
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Các thư viện Firebase để kết nối Realtime Database
import com.example.datsanbong.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Các thư viện MPAndroidChart để vẽ và cấu hình biểu đồ
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

// Các cấu trúc dữ liệu và công cụ định dạng (Java Utility)
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AdminRevenueActivity extends AppCompatActivity {

    private Button btnStartDate, btnEndDate;
    private TextView tvTotalRevenue, tvSuccessfulBookings, tvCanceledBookings;
    private BarChart barChartRevenue;

    private DatabaseReference mDatabase;
    private String startDate = "", endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        // Ánh xạ View
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvSuccessfulBookings = findViewById(R.id.tvSuccessfulBookings);
        tvCanceledBookings = findViewById(R.id.tvCanceledBookings);
        barChartRevenue = findViewById(R.id.barChartRevenue);

        // Kết nối Firebase tới node "Bookings"
        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        // Sự kiện chọn ngày (Bằng DatePickerDialog)
        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        // Mặc định ban đầu: Tải toàn bộ dữ liệu doanh thu từ trước đến nay
        loadRevenueData();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Định dạng tháng và ngày luôn có 2 chữ số (VD: 2026-06-25)
            String selectedDate = String.format("%d-%02d-%02d", year, (month + 1), dayOfMonth);

            if (isStartDate) {
                startDate = selectedDate;
                btnStartDate.setText("Từ: " + selectedDate);
            } else {
                endDate = selectedDate;
                btnEndDate.setText("Đến: " + selectedDate);
            }

            // Mỗi lần thay đổi bộ lọc ngày, tiến hành lọc lại dữ liệu
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                loadRevenueData();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void loadRevenueData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalRevenue = 0;
                int successCount = 0;
                int cancelCount = 0;

                // Dùng TreeMap thay vì HashMap để ngày tháng tự động sắp xếp tăng dần trên biểu đồ
                HashMap<String, Double> dailyRevenueMap = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        String date = booking.getNgayDat(); // Thuộc tính ngayDat từ lớp của bạn

                        // Lọc theo khoảng ngày Admin chọn
                        if (isDateInRange(date, startDate, endDate)) {
                            String status = booking.getTrangThai(); // Thuộc tính trangThai (PENDING, CONFIRMED, CANCELLED, COMPLETED)

                            if ("COMPLETED".equals(status)) {
                                totalRevenue += booking.getTongTien(); // Thuộc tính tongTien
                                successCount++;

                                // Cộng dồn doanh thu theo từng ngày
                                double currentDayRevenue = dailyRevenueMap.getOrDefault(date, 0.0);
                                dailyRevenueMap.put(date, currentDayRevenue + booking.getTongTien());

                            } else if ("CANCELLED".equals(status)) {
                                cancelCount++;
                            }
                        }
                    }
                }

                // Hiển thị số liệu lên giao diện
                DecimalFormat formatter = new DecimalFormat("#,###");
                tvTotalRevenue.setText(formatter.format(totalRevenue) + " VNĐ");
                tvSuccessfulBookings.setText(successCount + " đơn");
                tvCanceledBookings.setText(cancelCount + " đơn");

                // Vẽ lại biểu đồ cột
                setupBarChart(dailyRevenueMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminRevenueActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm phụ trợ so sánh chuỗi Ngày (Định dạng yyyy-MM-dd cho phép so sánh String trực tiếp qua hàm compareTo)
    private boolean isDateInRange(String bookingDate, String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return true; // Nếu chưa chọn bộ lọc thì hiển thị tất cả
        return bookingDate.compareTo(start) >= 0 && bookingDate.compareTo(end) <= 0;
    }

    // Thiết lập và đưa dữ liệu vào biểu đồ BarChart
    private void setupBarChart(HashMap<String, Double> revenueMap) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> sortedDates = new ArrayList<>(revenueMap.keySet());
        java.util.Collections.sort(sortedDates);
        int index = 0;
        // Duyệt qua map để đưa dữ liệu vào biểu đồ
        for (String date : sortedDates) {
            float revenue = revenueMap.get(date).floatValue();
            entries.add(new BarEntry(index, revenue));
            labels.add(date); // Nhãn ngày hiển thị dưới trục X
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu theo ngày");
        dataSet.setColor(Color.parseColor("#007A33"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartRevenue.setData(barData);

        // Định cấu hình trục X hiển thị ngày tháng
        XAxis xAxis = barChartRevenue.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45); // Xoay chữ cho đỡ bị đè nhau

        barChartRevenue.animateY(1000); // Hiệu ứng chạy cột mượt mà từ dưới lên
        barChartRevenue.invalidate(); // Refresh biểu đồ
    }
}
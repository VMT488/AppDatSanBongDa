package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.datsanbong.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AdminRevenueActivity extends AppCompatActivity {

    private Button btnStartDate, btnEndDate, btnGoToQuanLySan;
    private TextView tvTotalRevenue, tvSuccessfulBookings, tvCanceledBookings;
    private BarChart barChartRevenue;
    private DatabaseReference mDatabase;
    private String startDate = "", endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnGoToQuanLySan = findViewById(R.id.btnGoToQuanLySan);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvSuccessfulBookings = findViewById(R.id.tvSuccessfulBookings);
        tvCanceledBookings = findViewById(R.id.tvCanceledBookings);
        barChartRevenue = findViewById(R.id.barChartRevenue);

        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
        btnGoToQuanLySan.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRevenueActivity.this, AdminActivity.class);
            startActivity(intent);
        });
        loadRevenueData();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, dayOfMonth, month, year) -> {
            String selectedDate = String.format("%d-%02d-%02d", dayOfMonth, (month + 1), year);

            if (isStartDate) {
                startDate = selectedDate;
                btnStartDate.setText("Từ: " + selectedDate);
            } else {
                endDate = selectedDate;
                btnEndDate.setText("Đến: " + selectedDate);
            }

            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                loadRevenueData();
            }
        },  calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
        datePickerDialog.show();
    }

    private void loadRevenueData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalRevenue = 0;
                int successCount = 0;
                int cancelCount = 0;

                HashMap<String, Double> dailyRevenueMap = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        String date = booking.getNgayDat();

                        if (isDateInRange(date, startDate, endDate)) {
                            String status = booking.getTrangThai();
                            if ("COMPLETED".equals(status)) {
                                totalRevenue += booking.getTongTien();
                                successCount++;

                                double currentDayRevenue = dailyRevenueMap.getOrDefault(date, 0.0);
                                dailyRevenueMap.put(date, currentDayRevenue + booking.getTongTien());

                            } else if ("CANCELLED".equals(status)) {
                                cancelCount++;
                            }
                        }
                    }
                }

                DecimalFormat formatter = new DecimalFormat("#.###");
                tvTotalRevenue.setText(formatter.format(totalRevenue) + " Đ");
                tvSuccessfulBookings.setText(successCount + " đơn");
                tvCanceledBookings.setText(cancelCount + " đơn");

                setupBarChart(dailyRevenueMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminRevenueActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDateInRange(String bookingDate, String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return true;
        return bookingDate.compareTo(start) >= 0 && bookingDate.compareTo(end) <= 0;
    }

    // đưa dữ liệu vào biểu đồ BarChart
    private void setupBarChart(HashMap<String, Double> revenueMap) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> sortedDates = new ArrayList<>(revenueMap.keySet());
        Collections.sort(sortedDates);
        int index = 0;

        for (String date : sortedDates) {
            float revenue = revenueMap.get(date).floatValue();
            entries.add(new BarEntry(index, revenue));
            labels.add(date);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu theo ngày");
        dataSet.setColor(Color.parseColor("#007A33"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartRevenue.setData(barData);

        XAxis xAxis = barChartRevenue.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        barChartRevenue.animateY(1000);
        barChartRevenue.invalidate();
    }
}
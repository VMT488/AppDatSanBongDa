package com.example.datsanbong;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.datsanbong.models.Booking;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminRevenueActivity extends AppCompatActivity {

    private Button btnStartDate, btnEndDate;
    private TextView tvTotalRevenue, tvSuccessfulBookings, tvCanceledBookings;
    private BarChart barChartRevenue;
    private DatabaseReference mDatabase;
    private String startDate = "", endDate = "";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private final SimpleDateFormat sdfFirebase = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvSuccessfulBookings = findViewById(R.id.tvSuccessfulBookings);
        tvCanceledBookings = findViewById(R.id.tvCanceledBookings);
        barChartRevenue = findViewById(R.id.barChartRevenue);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_quan_ly_san) {
                startActivity(new Intent(AdminRevenueActivity.this, AdminActivity.class));
            } else if (id == R.id.nav_manage_customers) {
                startActivity(new Intent(AdminRevenueActivity.this, AdminCustomerActivity.class));
            } else if (id == R.id.nav_thong_ke) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Kết nối thẳng đến nhánh nút gốc "Bookings" trên Realtime DB
        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        loadRevenueData();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

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
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadRevenueData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalRevenue = 0;
                int successCount = 0;
                int cancelCount = 0;

                HashMap<String, Long> dailyRevenueMap = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Object valueDate = dataSnapshot.child("ngayDat").getValue();
                    String date = (valueDate != null) ? String.valueOf(valueDate) : "";

                    Object valueStatus = dataSnapshot.child("trangThai").getValue();
                    String status = (valueStatus != null) ? String.valueOf(valueStatus) : "";

                    long tongTien = 0;
                    Object valueTongTien = dataSnapshot.child("tongTien").getValue();
                    if (valueTongTien != null) {
                        if (valueTongTien instanceof Long) {
                            tongTien = (Long) valueTongTien;
                        } else if (valueTongTien instanceof Double) {
                            tongTien = ((Double) valueTongTien).longValue();
                        } else if (valueTongTien instanceof String) {
                            try {
                                tongTien = Long.parseLong((String) valueTongTien);
                            } catch (NumberFormatException e) {
                                tongTien = 0;
                            }
                        }
                    }

                    // 3. Kiểm tra điều kiện ngày tháng và trạng thái đơn hàng
                    if (!date.isEmpty() && isDateInRange(date, startDate, endDate)) {
                        if ("CONFIRMED".equals(status) || "COMPLETED".equals(status)) {
                            totalRevenue += tongTien;
                            successCount++;

                            long currentDayRevenue = dailyRevenueMap.getOrDefault(date, 0L);
                            dailyRevenueMap.put(date, currentDayRevenue + tongTien);

                        } else if ("CANCELLED".equals(status)) {
                            cancelCount++;
                        }
                    }
                }

                DecimalFormat formatter = new DecimalFormat("#,###");
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
        // Nếu admin chưa chọn bộ lọc ngày, mặc định trả về true để hiển thị tất cả dữ liệu lịch sử
        if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
            return true;
        }
        if (bookingDate == null || bookingDate.isEmpty()) {
            return false;
        }

        try {
            Date dateBooking = sdfFirebase.parse(bookingDate);
            Date dateStart = sdfFirebase.parse(start);
            Date dateEnd = sdfFirebase.parse(end);

            // Kiểm tra điều kiện lọt mốc thời gian bộ lọc
            return dateBooking != null && !dateBooking.before(dateStart) && !dateBooking.after(dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupBarChart(HashMap<String, Long> revenueMap) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> sortedDates = new ArrayList<>(revenueMap.keySet());

        // Sắp xếp ngày tháng tăng dần theo trục thời gian
        Collections.sort(sortedDates, (o1, o2) -> {
            try {
                return sdfFirebase.parse(o1).compareTo(sdfFirebase.parse(o2));
            } catch (ParseException e) {
                return 0;
            }
        });

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
        xAxis.setLabelRotationAngle(0);
        YAxis leftAxis = barChartRevenue.getAxisLeft();

        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(50000f);

        YAxis rightAxis = barChartRevenue.getAxisRight();
        rightAxis.setAxisMinimum(0f);

        barChartRevenue.getAxisLeft().setSpaceTop(15f);

        barChartRevenue.animateY(1000);
        barChartRevenue.invalidate();
    }
}
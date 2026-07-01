package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.adapters.AdminBookingAdapter;
import com.example.datsanbong.models.Booking;
import com.example.datsanbong.models.BookingStatus;
import com.example.datsanbong.services.RoleManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminBookingAdapter adapter;
    private DrawerLayout drawerLayout;
    private List<Booking> bookingList;
    private DatabaseReference bookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        new RoleManager().checkAdmin(admin -> {
            if (!admin) {

                Toast.makeText(
                        this,
                        "Bạn không có quyền truy cập",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }

        });

        recyclerView = findViewById(R.id.rvBooking);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        bookingList = new ArrayList<>();

        adapter = new AdminBookingAdapter(this, bookingList);

        recyclerView.setAdapter(adapter);

        bookingRef = FirebaseDatabase
                .getInstance()
                .getReference("Bookings");

        loadBookings();

        adapter.setOnStatusChangedListener((booking, newStatus) -> {

            if (!isValidStatus(booking.getTrangThai(), newStatus)) {

                Toast.makeText(
                        this,
                        "Không thể chuyển trạng thái này",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            bookingRef.child(booking.getId())
                    .child("trangThai")
                    .setValue(newStatus)
                    .addOnSuccessListener(unused ->

                            Toast.makeText(
                                    this,
                                    "Đã cập nhật trạng thái",
                                    Toast.LENGTH_SHORT
                            ).show())

                    .addOnFailureListener(e ->

                            Toast.makeText(
                                    this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show());

        });
        Toolbar toolbar = findViewById(R.id.toolbarBooking);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else if (id == R.id.nav_quan_ly_san) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminBookingActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_thong_ke) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminBookingActivity.this, AdminRevenueActivity.class);
                    startActivity(intent);
                }  else if(id == R.id.nav_manage_customers){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(AdminBookingActivity.this, AdminBookingActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_booking) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

    }

    private void loadBookings() {

        bookingRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        bookingList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            Booking booking =
                                    data.getValue(Booking.class);

                            if (booking != null) {

                                bookingList.add(booking);

                            }

                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                AdminBookingActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                });

    }

    /**
     * Kiểm tra luồng chuyển trạng thái
     */
    private boolean isValidStatus(
            String oldStatus,
            String newStatus) {

        if (oldStatus.equals(newStatus))
            return false;

        switch (oldStatus) {

            case BookingStatus.PENDING:

                return newStatus.equals(BookingStatus.CONFIRMED)
                        || newStatus.equals(BookingStatus.CANCELLED);

            case BookingStatus.CONFIRMED:

                return newStatus.equals(BookingStatus.COMPLETED);

            case BookingStatus.COMPLETED:

            case BookingStatus.CANCELLED:

                return false;
        }

        return false;
    }

}
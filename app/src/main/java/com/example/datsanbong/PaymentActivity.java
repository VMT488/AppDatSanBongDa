package com.example.datsanbong;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.datsanbong.models.Booking;
import com.example.datsanbong.models.KhungGio;
import com.example.datsanbong.models.SanBong;
import com.example.datsanbong.receivers.NotificationReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView txtPayTenSan, txtPayNgayCa, txtPayTongTien;
    private RadioGroup radioGroupPayment;
    private RadioButton radQR, radTienMat;
    private LinearLayout layoutQR;
    private ImageView imgQR;
    private Button btnXacNhanThanhToan;

    private String documentIdCuaSan, tenSan, ngayDat;
    private long gioBatDauLong, gioKetThucLong;
    private int sanBongId, viTriChon;
    private long giaSan;
    private String phuongThucThanhToan = "QR";
    private final String BANK_ID = "MB";
    private final String ACCOUNT_NO = "0383990265";
    private final String ACCOUNT_NAME = "TRAN TRUNG TIEN";

    private DatabaseReference mDatabaseSanBong;
    private DatabaseReference mDatabaseBookings;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Đã bật hệ thống nhắc nhở lịch đá bóng!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền thông báo để app nhắc lịch hẹn giờ đá!", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        kiemTraVaXinQuyenThongBao();
        String dbUrl = "https://datsanbong-b6ad1-default-rtdb.asia-southeast1.firebasedatabase.app/";
        mDatabaseSanBong = FirebaseDatabase.getInstance(dbUrl).getReference("DanhSachSanBong");
        mDatabaseBookings = FirebaseDatabase.getInstance(dbUrl).getReference("Bookings");

        txtPayTenSan = findViewById(R.id.txtPayTenSan);
        txtPayNgayCa = findViewById(R.id.txtPayNgayCa);
        txtPayTongTien = findViewById(R.id.txtPayTongTien);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radQR = findViewById(R.id.radQR);
        radTienMat = findViewById(R.id.radTienMat);
        layoutQR = findViewById(R.id.layoutQR);
        imgQR = findViewById(R.id.imgQR);
        btnXacNhanThanhToan = findViewById(R.id.btnXacNhanThanhToan);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            documentIdCuaSan = extras.getString("documentIdCuaSan");
            tenSan = extras.getString("tenSan");
            sanBongId = extras.getInt("sanBongId");
            giaSan = extras.getLong("giaSan");
            ngayDat = extras.getString("ngayDat");
            viTriChon = extras.getInt("viTriChon");
            gioBatDauLong = extras.getLong("gioBatDau");
            gioKetThucLong = extras.getLong("gioKetThuc");

            SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String strGioBatDau = sdfGio.format(new Date(gioBatDauLong));
            String strGioKetThuc = sdfGio.format(new Date(gioKetThucLong));

            txtPayTenSan.setText("Sân: " + tenSan);
            txtPayNgayCa.setText("Thời gian: " + strGioBatDau + " - " + strGioKetThuc + " (" + ngayDat + ")");
            txtPayTongTien.setText("Tổng tiền: " + giaSan + " đ");
        }

        taoMaQRThanhToan();

        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radQR) {
                phuongThucThanhToan = "QR";
                layoutQR.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radTienMat) {
                phuongThucThanhToan = "TIEN_MAT";
                layoutQR.setVisibility(View.GONE);
            }
        });

        btnXacNhanThanhToan.setOnClickListener(v -> tienHanhGhiNhanDatSan());
    }

    private void kiemTraVaXinQuyenThongBao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void taoMaQRThanhToan() {
        SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String strGioBatDau = sdfGio.format(new Date(gioBatDauLong));

        String noiDungChuyenKhoan = "Datsan " + tenSan.replaceAll("\\s+", "") + " " + strGioBatDau.replace(":", "h");
        String urlVietQR = "https://img.vietqr.io/image/" + BANK_ID + "-" + ACCOUNT_NO + "-compact.png"
                + "?amount=" + giaSan
                + "&addInfo=" + noiDungChuyenKhoan
                + "&accountName=" + ACCOUNT_NAME;

        Glide.with(this)
                .load(urlVietQR)
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(imgQR);
    }
    private void tienHanhGhiNhanDatSan() {
        if (documentIdCuaSan == null || documentIdCuaSan.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID Sân bóng!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnXacNhanThanhToan.setEnabled(false);

        mDatabaseSanBong.child(documentIdCuaSan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SanBong sanBongHienTai = snapshot.getValue(SanBong.class);
                    if (sanBongHienTai != null && sanBongHienTai.getDanhSachKhungGio() != null) {

                        List<KhungGio> danhSachCapNhat = sanBongHienTai.getDanhSachKhungGio();
                        if (viTriChon >= 0 && viTriChon < danhSachCapNhat.size()) {
                            danhSachCapNhat.get(viTriChon).setDaDat(true);
                            mDatabaseSanBong.child(documentIdCuaSan).child("danhSachKhungGio")
                                    .setValue(danhSachCapNhat)
                                    .addOnSuccessListener(aVoid -> {
                                        luuHoaDonBookingVaoRealtimeDB();
                                    })
                                    .addOnFailureListener(e -> {
                                        btnXacNhanThanhToan.setEnabled(true);
                                        Toast.makeText(PaymentActivity.this, "Lỗi cập nhật lịch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            btnXacNhanThanhToan.setEnabled(true);
                            Toast.makeText(PaymentActivity.this, "Lỗi: Vị trí ca chọn không hợp lệ!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    btnXacNhanThanhToan.setEnabled(true);
                    Toast.makeText(PaymentActivity.this, "Sân bóng này không tồn tại trên dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnXacNhanThanhToan.setEnabled(true);
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối DB: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void luuHoaDonBookingVaoRealtimeDB() {
        SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String strGioBatDau = sdfGio.format(new Date(gioBatDauLong));
        String strGioKetThuc = sdfGio.format(new Date(gioKetThucLong));

        String bookingId = mDatabaseBookings.push().getKey();

        if (bookingId == null) {
            btnXacNhanThanhToan.setEnabled(true);
            Toast.makeText(this, "Lỗi khởi tạo ID hóa đơn!", Toast.LENGTH_SHORT).show();
            return;
        }

        String trangThaiDonHang = phuongThucThanhToan.equals("QR") ? "CONFIRMED" : "PENDING";

        Booking newBooking = new Booking(
                bookingId,
                "USER_TEST_ID",
                sanBongId,
                tenSan,
                "Khách hàng Mobile (" + phuongThucThanhToan + ")",
                ngayDat,
                strGioBatDau,
                strGioKetThuc,
                giaSan,
                trangThaiDonHang,
                System.currentTimeMillis()
        );

        mDatabaseBookings.child(bookingId).setValue(newBooking)
                .addOnSuccessListener(unused -> {
                    datLichNhacNho(gioBatDauLong, tenSan);
                    Toast.makeText(PaymentActivity.this, "Đặt sân và thanh toán thành công!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnXacNhanThanhToan.setEnabled(true);
                    Toast.makeText(PaymentActivity.this, "Lỗi lưu Realtime DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void datLichNhacNho(long gioBatDauMilisecond, String tenSanBong) {
        long timeAlarmInMillis = gioBatDauMilisecond - (2 * 60 * 60 * 1000);

        if (timeAlarmInMillis < System.currentTimeMillis()) {
            return;
        }

        SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String strGioBatDau = sdfGio.format(new Date(gioBatDauMilisecond));

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("tenSan", tenSanBong);
        intent.putExtra("gioBatDau", strGioBatDau);

        int requestCode = tenSanBong.hashCode();
        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                flags
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, timeAlarmInMillis, 1000, pendingIntent);
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarmInMillis, pendingIntent);
            }
        }
    }
}
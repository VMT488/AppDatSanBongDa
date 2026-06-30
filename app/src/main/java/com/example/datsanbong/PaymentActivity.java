package com.example.datsanbong;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datsanbong.models.Booking;
import com.example.datsanbong.models.KhungGio;
import com.example.datsanbong.models.SanBong;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private TextView txtPayTenSan, txtPayNgayCa, txtPayTongTien;
    private RadioGroup radioGroupPayment;
    private RadioButton radQR, radTienMat;
    private LinearLayout layoutQR;
    private ImageView imgQR;
    private Button btnXacNhanThanhToan;

    private FirebaseFirestore db;
    private String documentIdCuaSan, tenSan, ngayDat, gioBatDau, gioKetThuc;
    private int sanBongId, viTriChon;
    private long giaSan;
    private String phuongThucThanhToan = "QR";
    private final String BANK_ID = "MB";
    private final String ACCOUNT_NO = "0383990265";
    private final String ACCOUNT_NAME = "TRAN TRUNG TIEN";
    private DatabaseReference realtimeDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();
        realtimeDb = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
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
            gioBatDau = extras.getString("gioBatDau");
            gioKetThuc = extras.getString("gioKetThuc");

            txtPayTenSan.setText("Sân: " + tenSan);
            txtPayNgayCa.setText("Thời gian: " + gioBatDau + " - " + gioKetThuc + " Đêm (" + ngayDat + ")");
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

    private void taoMaQRThanhToan() {
        String noiDungChuyenKhoan = "Datsan " + tenSan.replaceAll("\\s+", "") + " " + gioBatDau.replace(":", "h");

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
        btnXacNhanThanhToan.setEnabled(false);

        db.collection("DanhSachSanBong").document(documentIdCuaSan)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        SanBong sanBongHienTai = documentSnapshot.toObject(SanBong.class);
                        if (sanBongHienTai != null && sanBongHienTai.getDanhSachKhungGio() != null) {

                            List<KhungGio> danhSachCapNhat = sanBongHienTai.getDanhSachKhungGio();
                            danhSachCapNhat.get(viTriChon).setDaDat(true);

                            db.collection("DanhSachSanBong").document(documentIdCuaSan)
                                    .update("danhSachKhungGio", danhSachCapNhat)
                                    .addOnSuccessListener(aVoid -> {

                                        luuHoaDonBookingVaoRealtimeDB();
                                    })
                                    .addOnFailureListener(e -> {
                                        btnXacNhanThanhToan.setEnabled(true);
                                        Toast.makeText(PaymentActivity.this, "Lỗi cập nhật lịch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
    }
    private void luuHoaDonBookingVaoRealtimeDB() {
        String bookingId = realtimeDb.child("Bookings").push().getKey();

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
                gioBatDau,
                gioKetThuc,
                giaSan,
                trangThaiDonHang,
                System.currentTimeMillis()
        );

        //  đẩy object lên  Bookings
        realtimeDb.child("Bookings").child(bookingId).setValue(newBooking)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(PaymentActivity.this, "Đặt sân và thanh toán thành công!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnXacNhanThanhToan.setEnabled(true);
                    Toast.makeText(PaymentActivity.this, "Lỗi lưu Realtime DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
package com.example.datsanbong;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datsanbong.models.Booking;
import com.example.datsanbong.models.KhungGio;
import com.example.datsanbong.models.SanBong;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends BaseActivity {

    private ImageView imgSan;
    private TextView txtTenSan, txtDiaChi, txtGiaSan, txtNgayDat;
    private Button btnChonNgay, btnDatSan;
    private Spinner spinnerKhungGio;

    private FirebaseFirestore db;
    private SanBong sanBongHienTai;
    private String documentIdCuaSan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setupToolbar("Chi tiết sân");
        db = FirebaseFirestore.getInstance();


        imgSan = findViewById(R.id.imgDetailSan);
        txtTenSan = findViewById(R.id.txtDetailTenSan);
        txtDiaChi = findViewById(R.id.txtDetailDiaChi);
        txtGiaSan = findViewById(R.id.txtDetailGiaSan);
        txtNgayDat = findViewById(R.id.txtNgayDat);

        btnChonNgay = findViewById(R.id.btnChonNgay);
        btnDatSan = findViewById(R.id.btnDatSan);
        spinnerKhungGio = findViewById(R.id.spinnerKhungGio);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            txtTenSan.setText(bundle.getString("tenSan"));
            txtDiaChi.setText(bundle.getString("diaChi"));

            Object giaSanObj = bundle.get("giaSan");
            txtGiaSan.setText(giaSanObj != null ? giaSanObj.toString() + " đ/trận" : "");

            imgSan.setImageResource(bundle.getInt("hinhAnh"));

            documentIdCuaSan = bundle.getString("documentId");
        }

        if (documentIdCuaSan == null && txtTenSan.getText() != null) {
            documentIdCuaSan = txtTenSan.getText().toString().trim();
        }

        taiKhungGioRealtimeFromServer();

        btnChonNgay.setOnClickListener(v -> showDatePicker());
        btnDatSan.setOnClickListener(v -> xuLyDatSanFirebase());
    }

    private void taiKhungGioRealtimeFromServer() {
        if (documentIdCuaSan == null || documentIdCuaSan.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID mã sân!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("DanhSachSanBong").document(documentIdCuaSan)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) return;

                    List<KhungGio> listGio = new ArrayList<>();

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        sanBongHienTai = documentSnapshot.toObject(SanBong.class);
                        if (sanBongHienTai != null && sanBongHienTai.getDanhSachKhungGio() != null) {
                            listGio = sanBongHienTai.getDanhSachKhungGio();
                        }
                    }

                    if (listGio.isEmpty()) {
                        listGio = taoDanhSachCaMacDinh();
                        if (sanBongHienTai == null) {
                            int idInt = (int) (System.currentTimeMillis() / 1000);
                            sanBongHienTai = new SanBong(idInt, txtTenSan.getText().toString(), txtDiaChi.getText().toString(), 300000, "", listGio);
                            db.collection("DanhSachSanBong").document(documentIdCuaSan).set(sanBongHienTai);
                        } else {
                            sanBongHienTai.setDanhSachKhungGio(listGio);
                            db.collection("DanhSachSanBong").document(documentIdCuaSan).update("danhSachKhungGio", listGio);
                        }
                    }

                    CustomKhungGioAdapter adapter = new CustomKhungGioAdapter(
                            DetailActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            listGio
                    );
                    spinnerKhungGio.setAdapter(adapter);
                });
    }

    // Tạo sẵn 4 ca đá cố định dạng long bằng cách lấy mốc thời gian ngày hôm nay
    private List<KhungGio> taoDanhSachCaMacDinh() {
        List<KhungGio> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.set(Calendar.HOUR_OF_DAY, 16); long c1Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 17); cal.set(Calendar.MINUTE, 30); long c1End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_1", c1Start, c1End, false));

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 17); cal.set(Calendar.MINUTE, 30); long c2Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 19); cal.set(Calendar.MINUTE, 0); long c2End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_2", c2Start, c2End, false));

        cal.set(Calendar.HOUR_OF_DAY, 19); cal.set(Calendar.MINUTE, 0); long c3Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 20); cal.set(Calendar.MINUTE, 30); long c3End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_3", c3Start, c3End, true));

        cal.set(Calendar.HOUR_OF_DAY, 20); cal.set(Calendar.MINUTE, 30); long c4Start = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 22); cal.set(Calendar.MINUTE, 0); long c4End = cal.getTimeInMillis();
        list.add(new KhungGio("ca_4", c4Start, c4End, false));

        return list;
    }

    private void xuLyDatSanFirebase() {
        String ngayDat = txtNgayDat.getText().toString();
        if (ngayDat.equals("Chưa chọn ngày")) {
            Toast.makeText(this, "Vui lòng chọn ngày đá trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerKhungGio.getSelectedItem() == null) {
            Toast.makeText(this, "Chưa chọn khung giờ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int viTriChon = spinnerKhungGio.getSelectedItemPosition();
        KhungGio khungGioChon = (KhungGio) spinnerKhungGio.getSelectedItem();

        if (khungGioChon.isDaDat()) {
            Toast.makeText(this, "Ca này đã có người đặt!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.Intent intent = new android.content.Intent(DetailActivity.this, PaymentActivity.class);
        intent.putExtra("documentIdCuaSan", documentIdCuaSan);
        intent.putExtra("tenSan", sanBongHienTai.getTenSan());
        intent.putExtra("sanBongId", sanBongHienTai.getId());
        intent.putExtra("giaSan", sanBongHienTai.getGiaSan());
        intent.putExtra("ngayDat", ngayDat);
        intent.putExtra("viTriChon", viTriChon);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        intent.putExtra("gioBatDau", sdf.format(new Date(khungGioChon.getGioBatDau())));
        intent.putExtra("gioKetThuc", sdf.format(new Date(khungGioChon.getGioKetThuc())));

        startActivity(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    txtNgayDat.setText(date);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private class CustomKhungGioAdapter extends ArrayAdapter<KhungGio> {
        private final List<KhungGio> items;

        public CustomKhungGioAdapter(@NonNull android.content.Context context, int resource, @NonNull List<KhungGio> objects) {
            super(context, resource, objects);
            this.items = objects;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
            KhungGio kg = items.get(position);

            if (kg != null) {
                tv.setText(kg.toString());

                if (kg.isDaDat()) {
                    tv.setTextColor(Color.DKGRAY);
                    tv.setBackgroundColor(Color.parseColor("#DCDCDC"));
                } else {
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.WHITE);
                }
            }
            return tv;
        }

        @Override
        public boolean isEnabled(int position) {
            return !items.get(position).isDaDat();
        }
    }
}
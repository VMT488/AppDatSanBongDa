package com.example.datsanbong;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    private ImageView imgSan;
    private TextView txtTenSan, txtDiaChi, txtGiaSan, txtNgayDat;
    private Button btnChonNgay, btnDatSan;
    private Spinner spinnerGio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        imgSan = findViewById(R.id.imgDetailSan);
        txtTenSan = findViewById(R.id.txtDetailTenSan);
        txtDiaChi = findViewById(R.id.txtDetailDiaChi);
        txtGiaSan = findViewById(R.id.txtDetailGiaSan);
        txtNgayDat = findViewById(R.id.txtNgayDat);

        btnChonNgay = findViewById(R.id.btnChonNgay);
        btnDatSan = findViewById(R.id.btnDatSan);

        spinnerGio = findViewById(R.id.spinnerGio);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            txtTenSan.setText(bundle.getString("tenSan"));
            txtDiaChi.setText(bundle.getString("diaChi"));
            txtGiaSan.setText(bundle.getString("giaSan"));
            imgSan.setImageResource(bundle.getInt("hinhAnh"));
        }

        btnChonNgay.setOnClickListener(v -> showDatePicker());

        btnDatSan.setOnClickListener(v -> {

            String ngay = txtNgayDat.getText().toString();
            String gio = spinnerGio.getSelectedItem().toString();

            // xử lý đặt sân
        });

        String[] gioDa = {
                "06:00 - 08:00",
                "08:00 - 10:00",
                "10:00 - 12:00",
                "14:00 - 16:00",
                "16:00 - 18:00",
                "18:00 - 20:00",
                "20:00 - 22:00"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        gioDa
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerGio.setAdapter(adapter);
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String date =
                                    selectedDay + "/"
                                            + (selectedMonth + 1)
                                            + "/"
                                            + selectedYear;

                            txtNgayDat.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
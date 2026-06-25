package com.example.datsanbong;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    private ImageView imgSan;
    private TextView txtTenSan, txtDiaChi, txtGiaSan, txtNgayDat;
    private Button btnChonNgay, btnDatSan;
    private Spinner spinnerStartTime;
    private Spinner spinnerEndTime;

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

        spinnerStartTime = findViewById(R.id.spinnerStartTime);
        spinnerEndTime = findViewById(R.id.spinnerEndTime);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            txtTenSan.setText(bundle.getString("tenSan"));
            txtDiaChi.setText(bundle.getString("diaChi"));
            txtGiaSan.setText(bundle.getString("giaSan"));
            imgSan.setImageResource(bundle.getInt("hinhAnh"));
        }

        btnChonNgay.setOnClickListener(v -> showDatePicker());

        btnDatSan.setOnClickListener(v -> {

            String ngayDat = txtNgayDat.getText().toString();
            String gioBatDau =
                    spinnerStartTime.getSelectedItem().toString();

            String gioKetThuc =
                    spinnerEndTime.getSelectedItem().toString();

            Toast.makeText(
                    this,
                    "Ngày: " + ngayDat
                            + "\nTừ: " + gioBatDau
                            + "\nĐến: " + gioKetThuc,
                    Toast.LENGTH_LONG
            ).show();

        });

        ArrayList<String> startTimes = generateTimeSlots();

        ArrayAdapter<String> startAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        startTimes
                );

        startAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStartTime.setAdapter(startAdapter);

        spinnerStartTime.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        updateEndTime(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

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

    private ArrayList<String> generateTimeSlots() {

        ArrayList<String> list = new ArrayList<>();

        for(int hour = 7; hour <= 20; hour++) {

            list.add(String.format("%02d:00", hour));

            if(hour != 20){
                list.add(String.format("%02d:30", hour));
            }
        }

        return list;
    }
    private void updateEndTime(int startPosition) {

        ArrayList<String> allTimes = generateTimeSlots();

        ArrayList<String> endTimes = new ArrayList<>();

        for(int i = startPosition + 1; i < allTimes.size(); i++) {
            endTimes.add(allTimes.get(i));
        }

        ArrayAdapter<String> endAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        endTimes
                );

        endAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEndTime.setAdapter(endAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
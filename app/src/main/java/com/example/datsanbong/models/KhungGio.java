package com.example.datsanbong.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KhungGio {
    private String idKhungGio;
    private long gioBatDau;
    private long gioKetThuc;
    private boolean daDat;

    public KhungGio() {}

    public KhungGio(String idKhungGio, long gioBatDau, long gioKetThuc, boolean daDat) {
        this.idKhungGio = idKhungGio;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.daDat = daDat;
    }

    public String getIdKhungGio() { return idKhungGio; }
    public void setIdKhungGio(String idKhungGio) { this.idKhungGio = idKhungGio; }

    public long getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(long gioBatDau) { this.gioBatDau = gioBatDau; }

    public long getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(long gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public boolean isDaDat() { return daDat; }
    public void setDaDat(boolean daDat) { this.daDat = daDat; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String batDauStr = sdf.format(new Date(gioBatDau));
        String ketThucStr = sdf.format(new Date(gioKetThuc));

        return batDauStr + " - " + ketThucStr + (daDat ? " (Đã đặt)" : " (Còn trống)");
    }
}
package com.example.datsanbong.models;

public class Booking {
    private String id;
    private String userId;
    private int sanBongId;

    private String tenSan;
    private String tenNguoiDat;

    private String ngayDat;
    private String gioBatDau;
    private String gioKetThuc;

    private String tongTien;

    // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String trangThai;

    private long createdAt;

    public Booking() {
    }

    public Booking(String id,
                   String userId,
                   int sanBongId,
                   String tenSan,
                   String tenNguoiDat,
                   String ngayDat,
                   String gioBatDau,
                   String gioKetThuc,
                   String tongTien,
                   String trangThai,
                   long createdAt) {

        this.id = id;
        this.userId = userId;
        this.sanBongId = sanBongId;
        this.tenSan = tenSan;
        this.tenNguoiDat = tenNguoiDat;
        this.ngayDat = ngayDat;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSanBongId() {
        return sanBongId;
    }

    public void setSanBongId(int sanBongId) {
        this.sanBongId = sanBongId;
    }

    public String getTenSan() {
        return tenSan;
    }

    public void setTenSan(String tenSan) {
        this.tenSan = tenSan;
    }

    public String getTenNguoiDat() {
        return tenNguoiDat;
    }

    public void setTenNguoiDat(String tenNguoiDat) {
        this.tenNguoiDat = tenNguoiDat;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public String getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(String gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public String getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(String gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getTongTien() {
        return tongTien;
    }

    public void setTongTien(String tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

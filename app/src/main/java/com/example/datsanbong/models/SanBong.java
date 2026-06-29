package com.example.datsanbong.models;

import java.util.List;

public class SanBong {
    private int id;
    private String tenSan;
    private String diaChi;
    private long giaSan;
    private String hinhAnh;
    // Thay đổi ở đây: Một sân bóng có nhiều khung giờ để lựa chọn
    private List<KhungGio> danhSachKhungGio;

    public SanBong() {}

    public SanBong(int id, String tenSan, String diaChi, long giaSan, String hinhAnh, List<KhungGio> danhSachKhungGio) {
        this.id = id;
        this.tenSan = tenSan;
        this.diaChi = diaChi;
        this.giaSan = giaSan;
        this.hinhAnh = hinhAnh;
        this.danhSachKhungGio = danhSachKhungGio;
    }

    public List<KhungGio> getDanhSachKhungGio() { return danhSachKhungGio; }
    public void setDanhSachKhungGio(List<KhungGio> danhSachKhungGio) { this.danhSachKhungGio = danhSachKhungGio; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenSan() { return tenSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public long getGiaSan() { return giaSan; }
    public void setGiaSan(long giaSan) { this.giaSan = giaSan; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
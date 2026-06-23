package com.example.datsanbong.models;

public class SanBong {
    private int id;
    private String tenSan;
    private String diaChi;
    private String giaSan;
    private String hinhAnh; // Thay đổi từ int sang String để lưu URL ảnh

    // Constructor rỗng cho Firebase
    public SanBong() {
    }

    public SanBong(int id, String tenSan, String diaChi, String giaSan, String hinhAnh) {
        this.id = id;
        this.tenSan = tenSan;
        this.diaChi = diaChi;
        this.giaSan = giaSan;
        this.hinhAnh = hinhAnh;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenSan() { return tenSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getGiaSan() { return giaSan; }
    public void setGiaSan(String giaSan) { this.giaSan = giaSan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
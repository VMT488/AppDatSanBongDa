package com.example.datsanbong.models;

public class SanBong {
    private int id;
    private String tenSan;
    private String diaChi;
    private String giaSan;
    private int hinhAnh;

    public SanBong(int id, String tenSan, String diaChi, String giaSan, int hinhAnh) {
        this.id = id;
        this.tenSan = tenSan;
        this.diaChi = diaChi;
        this.giaSan = giaSan;
        this.hinhAnh = hinhAnh;
    }

    public int getId() { return id; }
    public String getTenSan() { return tenSan; }
    public String getDiaChi() { return diaChi; }
    public String getGiaSan() { return giaSan; }
    public int getHinhAnh() { return hinhAnh; }
}
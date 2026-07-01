package com.example.datsanbong.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.R;
import com.example.datsanbong.models.Booking;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.HistoryViewHolder> {

    private final List<Booking> mListBooking;

    public BookingHistoryAdapter(List<Booking> mListBooking) {
        this.mListBooking = mListBooking;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = mListBooking.get(position);
        if (booking == null) return;

        holder.txtTenSan.setText(booking.getTenSan());
        holder.txtThoiGian.setText("Khung giờ: " + booking.getGioBatDau() + " - " + booking.getGioKetThuc());
        holder.txtNgay.setText("Ngày đá: " + booking.getNgayDat());

        // Định dạng hiển thị tiền tệ VNĐ
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.txtTongTien.setText(formatter.format(booking.getTongTien()) + " VNĐ");

        // Xử lý giao diện màu sắc tag trạng thái
        String status = booking.getTrangThai() != null ? booking.getTrangThai() : "PENDING";
        holder.txtTrangThai.setText(status);

        GradientDrawable bgShape = new GradientDrawable();
        bgShape.setCornerRadius(12);

        switch (status) {
            case "CONFIRMED":
            case "COMPLETED":
                holder.txtTrangThai.setTextColor(Color.parseColor("#2E7D32")); // Xanh lá đậm
                bgShape.setColor(Color.parseColor("#E8F5E9")); // Xanh lá nhạt
                break;
            case "CANCELLED":
                holder.txtTrangThai.setTextColor(Color.parseColor("#C62828")); // Đỏ đậm
                bgShape.setColor(Color.parseColor("#FFEBEE")); // Đỏ nhạt
                break;
            default: // PENDING
                holder.txtTrangThai.setTextColor(Color.parseColor("#EF6C00")); // Cam đậm
                bgShape.setColor(Color.parseColor("#FFF3E0")); // Cam nhạt
                break;
        }
        holder.txtTrangThai.setBackground(bgShape);
    }

    @Override
    public int getItemCount() {
        return mListBooking != null ? mListBooking.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenSan, txtTrangThai, txtThoiGian, txtNgay, txtTongTien;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenSan = itemView.findViewById(R.id.txtHistoryTenSan);
            txtTrangThai = itemView.findViewById(R.id.txtHistoryTrangThai);
            txtThoiGian = itemView.findViewById(R.id.txtHistoryThoiGian);
            txtNgay = itemView.findViewById(R.id.txtHistoryNgay);
            txtTongTien = itemView.findViewById(R.id.txtHistoryTongTien);
        }
    }
}
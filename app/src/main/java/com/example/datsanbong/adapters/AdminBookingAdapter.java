package com.example.datsanbong.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.R;
import com.example.datsanbong.models.Booking;
import com.example.datsanbong.models.BookingStatus;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter
        extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private Context context;

    public interface OnStatusChangedListener{
        void onStatusChanged(Booking booking,String newStatus);
    }

    private OnStatusChangedListener listener;

    public void setOnStatusChangedListener(OnStatusChangedListener listener){
        this.listener = listener;
    }

    public AdminBookingAdapter(Context context, List<Booking> bookingList){
        this.context=context;
        this.bookingList=bookingList;

    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_admin,parent,false);

        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder,
                                 int position) {

        Booking booking = bookingList.get(position);

        holder.txtTenSan.setText(booking.getTenSan());

        holder.txtNguoiDat.setText(
                "Người đặt: " + booking.getTenNguoiDat());

        holder.txtNgayDat.setText(
                "Ngày: " + booking.getNgayDat());

        holder.txtKhungGio.setText(
                booking.getGioBatDau() + " - " +
                        booking.getGioKetThuc());

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("vi","VN"));

        holder.txtTongTien.setText(
                format.format(booking.getTongTien()));

        holder.txtTrangThai.setText(
                booking.getTrangThai());

        switch (booking.getTrangThai()){

            case BookingStatus.PENDING:
                holder.txtTrangThai.setTextColor(Color.parseColor("#FFA000"));
                break;

            case BookingStatus.CONFIRMED:
                holder.txtTrangThai.setTextColor(Color.parseColor("#1976D2"));
                break;

            case BookingStatus.COMPLETED:
                holder.txtTrangThai.setTextColor(Color.parseColor("#2E7D32"));
                break;

            case BookingStatus.CANCELLED:
                holder.txtTrangThai.setTextColor(Color.RED);
                break;
        }

        holder.btnDoiTrangThai.setOnClickListener(v -> showStatusDialog(booking));

    }

    private void showStatusDialog(Booking booking) {

        String[] status = {
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED,
                BookingStatus.COMPLETED,
                BookingStatus.CANCELLED
        };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);

        builder.setTitle("Chọn trạng thái");

        builder.setItems(status, (dialog, which) -> {

            if (listener != null) {
                listener.onStatusChanged(
                        booking,
                        status[which]
                );
            }

        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView txtTenSan;
        TextView txtNguoiDat;
        TextView txtNgayDat;
        TextView txtKhungGio;
        TextView txtTongTien;
        TextView txtTrangThai;

        Button btnDoiTrangThai;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTenSan=itemView.findViewById(R.id.txtTenSan);
            txtNguoiDat=itemView.findViewById(R.id.txtNguoiDat);
            txtNgayDat=itemView.findViewById(R.id.txtNgayDat);
            txtKhungGio=itemView.findViewById(R.id.txtKhungGio);
            txtTongTien=itemView.findViewById(R.id.txtTongTien);
            txtTrangThai=itemView.findViewById(R.id.txtTrangThai);
            btnDoiTrangThai=itemView.findViewById(R.id.btnDoiTrangThai);

        }
    }
}
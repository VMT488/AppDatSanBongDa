package com.example.datsanbong.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.datsanbong.R;
import com.example.datsanbong.models.SanBong;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminSanBongAdapter extends RecyclerView.Adapter<AdminSanBongAdapter.AdminViewHolder> {

    private final List<SanBong> mListSanBong;
    private final List<String> mListDocumentIds;
    private final OnAdminItemClickListener listener;

    public interface OnAdminItemClickListener {
        void onXoaClick(String documentId, int position);
    }

    public AdminSanBongAdapter(List<SanBong> mListSanBong, List<String> mListDocumentIds, OnAdminItemClickListener listener) {
        this.mListSanBong = mListSanBong;
        this.mListDocumentIds = mListDocumentIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_san_bong_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        SanBong sanBong = mListSanBong.get(position);
        String docId = mListDocumentIds.get(position);
        if (sanBong == null) return;

        Glide.with(holder.itemView.getContext()).load(sanBong.getHinhAnh()).placeholder(android.R.drawable.ic_menu_gallery).into(holder.imgSanBong);
        holder.txtTenSan.setText(sanBong.getTenSan());
        holder.txtDiaChi.setText(sanBong.getDiaChi());
        NumberFormat formatter =
                NumberFormat.getInstance(new Locale("vi", "VN"));

        holder.txtGiaSan.setText(
                formatter.format(sanBong.getGiaSan()) + " VNĐ");

        // Đổ dữ liệu khung giờ lên giao diện công khai
        if(sanBong.getKhungGio() != null && !sanBong.getKhungGio().isEmpty()){
            holder.txtKhungGio.setText("Giờ: " + sanBong.getKhungGio());
        } else {
            holder.txtKhungGio.setText("Giờ: Chưa cập nhật");
        }

        holder.btnXoa.setOnClickListener(v -> {
            if (listener != null) {
                listener.onXoaClick(docId, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListSanBong != null ? mListSanBong.size() : 0;
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgSanBong;
        private final TextView txtTenSan, txtDiaChi, txtGiaSan, txtKhungGio;
        private final Button btnXoa;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSanBong = itemView.findViewById(R.id.imgAdminSanBong);
            txtTenSan = itemView.findViewById(R.id.txtAdminTenSan);
            txtDiaChi = itemView.findViewById(R.id.txtAdminDiaChi);
            txtGiaSan = itemView.findViewById(R.id.txtAdminGiaSan);
            txtKhungGio = itemView.findViewById(R.id.txtAdminKhungGio);
            btnXoa = itemView.findViewById(R.id.btnAdminXoa);
        }
    }
}
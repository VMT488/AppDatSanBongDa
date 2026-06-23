package com.example.datsanbong.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Thư viện dùng để tải ảnh từ URL Firebase
import com.example.datsanbong.R;
import com.example.datsanbong.models.SanBong;
import java.util.List;

public class SanBongAdapter extends RecyclerView.Adapter<SanBongAdapter.SanBongViewHolder> {

    // Sửa cảnh báo bằng cách thêm 'final' cho mListSanBong
    private final List<SanBong> mListSanBong;

    public SanBongAdapter(List<SanBong> mListSanBong) {
        this.mListSanBong = mListSanBong;
    }

    @NonNull
    @Override
    public SanBongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_san_bong, parent, false);
        return new SanBongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SanBongViewHolder holder, int position) {
        SanBong sanBong = mListSanBong.get(position);
        if (sanBong == null) return;

        // ĐÃ SỬA LỖI: Sử dụng Glide để load chuỗi String URL từ Firebase thay vì setImageResource
        Glide.with(holder.itemView.getContext())
                .load(sanBong.getHinhAnh()) // getHinhAnh() lúc này trả về String URL
                .placeholder(R.mipmap.ic_launcher) // Ảnh hiển thị tạm thời trong lúc đợi tải từ mạng
                .error(R.mipmap.ic_launcher) // Ảnh hiển thị nếu link ảnh bị lỗi
                .into(holder.imgSanBong);

        holder.txtTenSan.setText(sanBong.getTenSan());
        holder.txtDiaChi.setText(sanBong.getDiaChi());
        holder.txtGiaSan.setText(sanBong.getGiaSan());
    }

    @Override
    public int getItemCount() {
        if (mListSanBong != null) return mListSanBong.size();
        return 0;
    }

    public static class SanBongViewHolder extends RecyclerView.ViewHolder {
        // Sửa cảnh báo bằng cách thêm 'final' cho các View
        private final ImageView imgSanBong;
        private final TextView txtTenSan;
        private final TextView txtDiaChi;
        private final TextView txtGiaSan;

        public SanBongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSanBong = itemView.findViewById(R.id.imgSanBong);
            txtTenSan = itemView.findViewById(R.id.txtTenSan);
            txtDiaChi = itemView.findViewById(R.id.txtDiaChi);
            txtGiaSan = itemView.findViewById(R.id.txtGiaSan);
        }
    }
}
package com.example.datsanbong.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.datsanbong.R;
import com.example.datsanbong.models.SanBong;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanBongAdapter extends RecyclerView.Adapter<SanBongAdapter.SanBongViewHolder> {

    private List<SanBong> mListSanBong;
    private List<SanBong> mListSanBongGoc = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SanBong sanBong);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SanBongAdapter(List<SanBong> mListSanBong) {
        this.mListSanBong = mListSanBong;
    }

    public void setDanhSachGoc(List<SanBong> list) {
        this.mListSanBongGoc = new ArrayList<>(list);
    }

    public void filter(String query) {
        List<SanBong> listFilter = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            listFilter.addAll(mListSanBongGoc);
        } else {
            String textSearch = query.toLowerCase().trim();
            for (SanBong san : mListSanBongGoc) {
                if (san.getTenSan() != null && san.getTenSan().toLowerCase().contains(textSearch)) {
                    listFilter.add(san);
                }
            }
        }

        this.mListSanBong = listFilter;
        notifyDataSetChanged();
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
        String imageStr = sanBong.getHinhAnh();
        if (imageStr != null && (imageStr.startsWith("http://") || imageStr.startsWith("https://"))) {
            Glide.with(holder.itemView.getContext())
                    .load(imageStr)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgSanBong);
        } else if (imageStr != null && !imageStr.isEmpty()) {
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    imageStr, "drawable", holder.itemView.getContext().getPackageName());

            if (resId != 0) {
                holder.imgSanBong.setImageResource(resId);
            } else {
                holder.imgSanBong.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            holder.imgSanBong.setImageResource(R.mipmap.ic_launcher);
        }
        holder.txtTenSan.setText(sanBong.getTenSan());
        holder.txtDiaChi.setText(sanBong.getDiaChi());
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        holder.txtGiaSan.setText(formatter.format(sanBong.getGiaSan()) + " VNĐ");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sanBong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListSanBong != null ? mListSanBong.size() : 0;
    }

    public static class SanBongViewHolder extends RecyclerView.ViewHolder {
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
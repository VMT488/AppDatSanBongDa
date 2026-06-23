package com.example.datsanbong.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datsanbong.R;
import com.example.datsanbong.models.SanBong;
import java.util.List;

public class SanBongAdapter extends RecyclerView.Adapter<SanBongAdapter.SanBongViewHolder> {

    private List<SanBong> mListSanBong;
    public interface OnItemClickListener{
        void onItemClick(SanBong sanBong);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
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

        holder.imgSanBong.setImageResource(sanBong.getHinhAnh());
        holder.txtTenSan.setText(sanBong.getTenSan());
        holder.txtDiaChi.setText(sanBong.getDiaChi());
        holder.txtGiaSan.setText(sanBong.getGiaSan());

        holder.itemView.setOnClickListener(v -> {

            if(listener != null){
                listener.onItemClick(sanBong);
            }

        });
    }

    @Override
    public int getItemCount() {
        if (mListSanBong != null) return mListSanBong.size();
        return 0;
    }

    public static class SanBongViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSanBong;
        private TextView txtTenSan, txtDiaChi, txtGiaSan;

        public SanBongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSanBong = itemView.findViewById(R.id.imgSanBong);
            txtTenSan = itemView.findViewById(R.id.txtTenSan);
            txtDiaChi = itemView.findViewById(R.id.txtDiaChi);
            txtGiaSan = itemView.findViewById(R.id.txtGiaSan);
        }
    }
}
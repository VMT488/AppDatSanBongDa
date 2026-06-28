package com.example.datsanbong.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datsanbong.R;
import com.example.datsanbong.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user != null) {
            holder.tvName.setText(user.getName());
            holder.tvUsername.setText("Tài khoản: " + user.getUsername());
            holder.tvPhone.setText("SĐT: " + (user.getPhone().isEmpty() ? "Chưa cập nhật" : user.getPhone()));
            holder.tvEmail.setText("Email: " + user.getEmail());

            if (user.isActive()) {
                holder.tvStatus.setText("Hoạt động");
                holder.tvStatus.setTextColor(Color.parseColor("#007A33"));
            } else {
                holder.tvStatus.setText("Bị khóa");
                holder.tvStatus.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername, tvPhone, tvEmail, tvStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvUsername = itemView.findViewById(R.id.tvUserUsername);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
        }
    }
}
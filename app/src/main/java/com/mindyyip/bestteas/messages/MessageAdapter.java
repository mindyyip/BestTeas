package com.mindyyip.bestteas.messages;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindyyip.bestteas.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViews> {
    private List<Messages> messagesList;
    private Context context;
    public static final int message_left_amt = 0;
    public static final int message_right_amt = 1;
    private String imgUrl;
    FirebaseUser user;


    public MessageAdapter(List<Messages> messagesList, Context context, String imgUrl) {
        this.messagesList = messagesList;
        this.context = context;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public MessageViews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //controls the layout
        View layoutView;
        if (viewType == message_right_amt) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right, null, false);
        }
        else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left, null, false);
        }
//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right, null, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(params);
        MessageViews messageViews = new MessageViews(layoutView);
        return messageViews;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViews holder, int position) {
        holder.showMessage.setText(messagesList.get(position).getMessage());
        if (imgUrl.equals("default")) {
            holder.profilePicture.setImageResource(R.mipmap.default_profile);
        }
        else {
            Glide.with(context).load(imgUrl).into(holder.profilePicture);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (messagesList.get(position).getIsCreator().equals(true)) {
            return message_right_amt;
        }
        else {
            return message_left_amt;
        }
    }
}

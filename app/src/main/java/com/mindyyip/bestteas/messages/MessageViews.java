package com.mindyyip.bestteas.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindyyip.bestteas.R;

public class MessageViews extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView showMessage;
    public ImageView profilePicture;
    public MessageViews(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        showMessage = itemView.findViewById(R.id.showMessage);
        profilePicture = itemView.findViewById(R.id.profilePicture);
    }

    @Override
    public void onClick(View view) {

    }
}

package com.mindyyip.bestteas.matches;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindyyip.bestteas.R;
import com.mindyyip.bestteas.messages.MessageActivity;

public class MatchesViews extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView matchId, matchName;
    public ImageView matchPic;
    public MatchesViews(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        matchId = (TextView)itemView.findViewById(R.id.matchId);
        matchName = (TextView)itemView.findViewById(R.id.matchName);
        matchPic = (ImageView)itemView.findViewById(R.id.matchPic);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchId", matchId.getText().toString());
        bundle.putString("matchPic", matchPic.toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}

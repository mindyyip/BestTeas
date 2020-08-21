package com.mindyyip.bestteas.matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindyyip.bestteas.R;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchesViews> {
    private List<Matches> matchesList;
    private Context context;

    //passes the info from MatchesActivity to MatchesAdapter
    public MatchAdapter(List<Matches> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //controls the layout
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_layout, null, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(params);
        MatchesViews matchViews = new MatchesViews(layoutView);
        return matchViews;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViews holder, int position) {
        //populates the layout that we pass when notified in MatchesActivity
        holder.matchId.setText(matchesList.get(position).getUserId());
        holder.matchName.setText(matchesList.get(position).getName());
        if (!matchesList.get(position).getProfilePic().equals("default")) {
            Glide.with(context).load(matchesList.get(position).getProfilePic()).into(holder.matchPic);
        }

    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}

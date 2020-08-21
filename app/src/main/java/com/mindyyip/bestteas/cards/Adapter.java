package com.mindyyip.bestteas.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.mindyyip.bestteas.R;
import com.mindyyip.bestteas.cards.Cards;

public class Adapter extends ArrayAdapter {
    Context context;
    public Adapter(Context context, int resourceId, List<Cards>items) {
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        Cards card = (Cards) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView bio = (TextView) convertView.findViewById(R.id.bio);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        name.setText(card.getName());
        bio.setText(card.getBio());
        switch(card.getProfilePicUrl()) {
            case "default":
                Glide.with(getContext()).load(R.mipmap.default_profile).into(image);
                break;
            default:
                Glide.with(getContext()).load(card.getProfilePicUrl()).into(image);
                break;
        }

        return convertView;

    }

}

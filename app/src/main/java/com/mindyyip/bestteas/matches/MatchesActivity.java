package com.mindyyip.bestteas.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindyyip.bestteas.R;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter matchAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String currentMatchId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        currentMatchId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager((MatchesActivity.this));
        recyclerView.setLayoutManager(layoutManager);

        matchAdapter = new MatchAdapter(getMatchData(), MatchesActivity.this);
        recyclerView.setAdapter(matchAdapter);
        getMatch();

    }

    private void getMatch() {
        DatabaseReference matchData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentMatchId).child("swipes").child("matches");
        matchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()) {
                        getMatchInfo(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMatchInfo(String key) {
        DatabaseReference matchData = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        matchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userId = snapshot.getKey();
                    String name = "";
                    String profilePic = "";
                    if (snapshot.child("name").getValue() != null) {
                        name = snapshot.child("name").getValue().toString();
                    }
                    if (snapshot.child("profilePictureUrl").getValue() != null) {
                        profilePic = snapshot.child("profilePictureUrl").getValue().toString();
                    }
                    Matches match = new Matches(userId, name, profilePic);
                    results.add(match);
                    matchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<Matches> results = new ArrayList<Matches>();
    private List<Matches> getMatchData() {
        return results;
    }
}
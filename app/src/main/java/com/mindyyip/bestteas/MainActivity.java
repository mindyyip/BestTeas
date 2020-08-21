package com.mindyyip.bestteas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import com.mindyyip.bestteas.cards.Adapter;
import com.mindyyip.bestteas.cards.Cards;
import com.mindyyip.bestteas.matches.MatchesActivity;

public class MainActivity extends AppCompatActivity {
    private Adapter adapter;
    private FirebaseAuth auth;
    private String mainUserId;
    private String userSchool;
    private String currentCardId;
    private DatabaseReference userData;
    List<Cards> rows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        mainUserId = auth.getCurrentUser().getUid();
        userData = FirebaseDatabase.getInstance().getReference().child("Users");
        findSchool();
        rows = new ArrayList<Cards>();
        adapter = new Adapter(this, R.layout.item, rows);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rows.remove(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards card = (Cards) dataObject;
                String currentCardId = card.getUserId();
                userData.child(mainUserId).child("swipes").child("dislike").child(currentCardId).setValue(true);
                userData.child(currentCardId).child("seenBy").child(mainUserId).setValue(true);
                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards card = (Cards) dataObject;
                currentCardId = card.getUserId();
                userData.child(mainUserId).child("swipes").child("like").child(currentCardId).setValue(true);
                userData.child(currentCardId).child("seenBy").child(mainUserId).setValue(true);
                isMatch();
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void findSchool(){
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(mainUserId);
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.child("school").getValue() != null) {
                        userSchool = snapshot.child("school").getValue().toString();
                        getUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUsers() {
        userData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("school").getValue() != null) {
                    if (snapshot.exists() && !(snapshot.getKey().equals(mainUserId))) {
                        if (!snapshot.child("seenBy").hasChild(mainUserId) && snapshot.child("school").getValue().toString().equals(userSchool)) {
                            String profilePictureUrl = "default";
                            if (!snapshot.child("profilePictureUrl").getValue().equals("default")) {
                                profilePictureUrl = snapshot.child("profilePictureUrl").getValue().toString();
                            }
                            Cards card = new Cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profilePictureUrl, snapshot.child("bio").getValue().toString());
                            rows.add(card);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void isMatch() {
        DatabaseReference currentCardConnections = userData.child(currentCardId).child("swipes").child("like").child(mainUserId);
        currentCardConnections.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Match!", Toast.LENGTH_LONG).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Messages").push().getKey();

                    userData.child(currentCardId).child("swipes").child("matches").child(mainUserId).setValue(true);
                    userData.child(mainUserId).child("swipes").child("matches").child(currentCardId).setValue(true);

                    userData.child(currentCardId).child("swipes").child("matches").child(mainUserId).child("messageID").setValue(key);
                    userData.child(mainUserId).child("swipes").child("matches").child(currentCardId).child("messageID").setValue(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void getSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        adapter.notifyDataSetChanged();
    }

    public void getMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginOrRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
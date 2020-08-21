package com.mindyyip.bestteas.messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindyyip.bestteas.R;
import com.mindyyip.bestteas.matches.MatchAdapter;
import com.mindyyip.bestteas.matches.Matches;
import com.mindyyip.bestteas.matches.MatchesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter messageAdapter;
    private LinearLayoutManager layoutManager;
    private EditText messageText;
    private Button sendButton;
    private String currentMatchId, mainUserId, messageId, imgUrl, profilePicture;
    private List<Message> message;
    DatabaseReference messageUserData, messageData, currentMatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mainUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentMatchId = getIntent().getExtras().getString("matchId");
        profilePicture = getIntent().getExtras().getString("matchPic");
        messageUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(mainUserId).child("swipes").child("matches").child(currentMatchId).child("messageID");
        messageData = FirebaseDatabase.getInstance().getReference().child("Messages");
        getMessageId();
        currentMatch = FirebaseDatabase.getInstance().getReference().child("Users").child(currentMatchId).child("profilePictureUrl");
        imgUrl = currentMatch.getKey();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager((MessageActivity.this));
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        messageAdapter = new MessageAdapter(getMessageData(), MessageActivity.this, profilePicture);
        recyclerView.setAdapter(messageAdapter);
        messageText = (EditText) findViewById(R.id.message);
        sendButton = findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


    }

    private void getMessageId() {
        messageUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    messageId = snapshot.getValue().toString();
                    messageData = messageData.child(messageId);
                    getMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessages() {
        messageData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String message = "";
                    String creator = "";
                    if (snapshot.child("text").getValue() != null) {
                        message = snapshot.child("text").getValue().toString();
                    }
                    if (snapshot.child("creator").getValue() != null) {
                        creator = snapshot.child("creator").getValue().toString();
                    }
                    boolean isCreator = false;
                    if (creator.equals(mainUserId)) {
                        isCreator = true;

                    }
                    Messages newMessage = new Messages(message, isCreator);
                    results.add(newMessage);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);


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

    private void sendMessage() {
        String message = messageText.getText().toString();
        if (!message.isEmpty()) {
            DatabaseReference newMessageData = messageData.push();
            Map newMessage = new HashMap();
            newMessage.put("creator", mainUserId);
            newMessage.put("text", message);
            newMessageData.setValue(newMessage);
        }
        messageText.setText(null);
    }

    private ArrayList<Messages> results = new ArrayList<Messages>();
    private List<Messages> getMessageData() {
        return results;
    }
}
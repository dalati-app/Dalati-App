package com.dalati.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalati.R;
import com.dalati.ui.adapters.MessageAdapter;
import com.dalati.ui.models.Chat;
import com.dalati.ui.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChattingActivity extends BaseActivity {
    List<ChatMessage> chatMessageList;
    MessageAdapter messageAdapter;
    RecyclerView chat_recyclerView;
    DatabaseReference databaseReference;
    EditText etMessage;
    ImageView btnSend;
    Calendar calForDate;
    TextView textName;
    FirebaseAuth firebaseAuth;
    String userId, anotherId = "test", name = "None";
    AppCompatImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chat_recyclerView = findViewById(R.id.chatRecyclerView);
        etMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        textName = findViewById(R.id.textName);
        btnBack = findViewById(R.id.imageBack);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
     //   anotherId = getIntent().getStringExtra("anotherId");
        name = getIntent().getStringExtra("name");
        textName.setText(name);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        calForDate = Calendar.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatting").child("Messages");

        chat_recyclerView.setHasFixedSize(true);
        chat_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatMessageList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        ChatMessage chatMessage = postSnapshot.getValue(ChatMessage.class);
                        if (chatMessage.getSenderId().equals(userId) || chatMessage.getReceiverId().equals(userId)) {
                            if (chatMessage.getSenderId().equals(anotherId)
                                    || chatMessage.getReceiverId().equals(anotherId)) {
                                chatMessageList.add(chatMessage);
                            }
                        }
                    }


                messageAdapter = new MessageAdapter(ChattingActivity.this, chatMessageList);
                chat_recyclerView.setAdapter(messageAdapter);
                chat_recyclerView.scrollToPosition(chatMessageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
                String currentDate = currentDateFormat.format(calForDate.getTime());
                ChatMessage chatMessage = new ChatMessage(userId, anotherId, message, currentDate);

                String messageId = databaseReference.push().getKey();
                databaseReference.child(messageId).setValue(chatMessage);
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ChatList");

                Chat chat = new Chat(userId, userId, message, currentDate);
                databaseReference1.child(anotherId).child(userId).setValue(chat);
                etMessage.setText("");


            }
        });

    }

    @Override
    public int defineLayout() {
        return R.layout.activity_chatting;
    }
}
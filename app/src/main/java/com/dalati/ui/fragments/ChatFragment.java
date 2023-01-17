package com.dalati.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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

    View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        defineViews();
        getChats();
        return view;
    }

    private void defineViews() {
        chat_recyclerView = view.findViewById(R.id.chatRecyclerView);
        etMessage = view.findViewById(R.id.inputMessage);
        btnSend = view.findViewById(R.id.btnSend);
        textName = view.findViewById(R.id.textName);
        btnBack = view.findViewById(R.id.imageBack);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        //   anotherId = getIntent().getStringExtra("anotherId");
        textName.setText(R.string.offices);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        calForDate = Calendar.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatting").child("Messages");

        chat_recyclerView.setHasFixedSize(true);
        chat_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMessageList = new ArrayList<>();
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

    private void getChats() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        ChatMessage chatMessage = postSnapshot.getValue(ChatMessage.class);
                        if (chatMessage.getSenderId().equals(userId) || chatMessage.getReceiverId().equals(userId)) {
                            if (chatMessage.getSenderId().equals(anotherId)
                                    || chatMessage.getReceiverId().equals(anotherId)) {
                                chatMessageList.add(chatMessage);
                            }
                        }
                    }


                    messageAdapter = new MessageAdapter(getActivity(), chatMessageList);
                    chat_recyclerView.setAdapter(messageAdapter);
                    chat_recyclerView.scrollToPosition(chatMessageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
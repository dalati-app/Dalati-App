package com.dalati.ui.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dalati.R;
import com.dalati.ui.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ChatMessage> mChat;
    private String imageurl;
    String date;
    DateFormat formatter = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    ;


    public MessageAdapter(Context mContext, List<ChatMessage> mChat) {
        this.mChat = mChat;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_sent_message, parent, false);
            System.out.println("You're right");
            return new ViewHolder(view);
        } else if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_reciver_message, parent, false);
            System.out.println("You're left");
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_reciver_message, parent, false);
            System.out.println("You're right default");
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatMessage chat = mChat.get(position);

        String url = "https://images.unsplash.com/photo-1553095066-5014bc7b7f2d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8d2FsbCUyMGJhY2tncm91bmR8ZW58MHx8MHx8&w=1000&q=80";
        holder.show_message.setText(chat.getMessage());

        if (getItemViewType(position) == MSG_TYPE_LEFT) {

            Glide.with(mContext)
                    .load(url)
                    .centerCrop()
                    .into(holder.profile_image);
        } else {
            holder.profile_image.setVisibility(View.GONE);
        }

        date = "";
        try {
            //   Date messageDate = formatter.parse(chat.getDateTime());
            Date date1 = formatter1.parse(chat.getDateTime());
            date = formatter.format(date1);
            holder.textDateTime.setText(date);


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public TextView textDateTime;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.textMessage);
            profile_image = itemView.findViewById(R.id.imageProfile);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            //  firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSenderId().equals(userId)) {
            System.out.println(position + ": He is Sender");
            return MSG_TYPE_RIGHT;
        } else {
            System.out.println(position + ": He is Rec");
            return MSG_TYPE_LEFT;
        }
    }
}
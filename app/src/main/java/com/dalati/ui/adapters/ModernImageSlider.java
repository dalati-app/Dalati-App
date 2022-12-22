package com.dalati.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalati.R;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.List;

public class ModernImageSlider extends RecyclerView.Adapter<ModernImageSlider.TravelLocationViewHolder> {
    private List<Uri> imagesList;
    Context mContext;

    public ModernImageSlider(List<Uri> imagesList, Context mContext) {
        this.imagesList = imagesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TravelLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TravelLocationViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TravelLocationViewHolder holder, int position) {

     /*   holder.textTitle.setText(travelLocation.getName());
        holder.textLocation.setText(travelLocation.getLibrary_id());*/

       /* Glide.with(mContext)
                .load(imagesList.get(position))
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.img);
    }*/

        holder.img.setImageURI(imagesList.get(position));
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    static class TravelLocationViewHolder extends RecyclerView.ViewHolder {
        private KenBurnsView img;

        TravelLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);

        }


    }
}
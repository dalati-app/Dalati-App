package com.dalati.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dalati.R;
import com.dalati.ui.activities.ReportDetailsActivity;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ImageViewHolder> {

    Context mContext;
    List<Report> reportList;
    private Context context;
    int type = 0;
    List<Category> categoryList = new ArrayList<>();
    List<Type> typeList = new ArrayList<>();
    SQLiteDatabase db;


    public ReportAdapter(Context context) {
        this.mContext = context;
        getCategory();
        getTypes();
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.report_item, parent, false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Report reportObj = reportList.get(position);


        if (reportObj.getImages() == null) {

        } else {
            Glide.with(mContext)
                    .load(reportObj.getImages().get(0))
                    .centerCrop()
                    .into(holder.reportImage);
        }

        if (typeList != null && categoryList != null && reportList != null) {
            for (Type type : typeList) {

                if (type.getId().equals(reportObj.getType_id())) {
                    holder.tvType.setText(type.getNameEn());
                }
            }

            for (Category category : categoryList) {
                if (category.getId().equals(reportObj.getCategory_id())) {
                    holder.tvCategory.setText(category.getNameEn());
                }
            }
        }
        holder.tvDate.setText(reportObj.getDate());
        holder.tvDescription.setText(reportObj.getDescription());

        holder.cvReport.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_recycler4));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Report reportObj = reportList.get(position);
                Intent intent = new Intent(mContext, ReportDetailsActivity.class);
                intent.putExtra("report", (Serializable) reportObj);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


    }


    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
        notifyDataSetChanged();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }

    public void getCategory() {
        try {
            db = mContext.openOrCreateDatabase("local", Context.MODE_PRIVATE, null);


            Cursor c = db.rawQuery("select * from categories", null);

            while (c.moveToNext()) {

                categoryList.add(new Category(c.getString(0), c.getString(2), c.getString(1)));
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getTypes() {
        try {
            db = mContext.openOrCreateDatabase("local", Context.MODE_PRIVATE, null);


            Cursor c = db.rawQuery("select * from types", null);

            while (c.moveToNext()) {

                typeList.add(new Type(c.getString(0), c.getString(2), c.getString(1), c.getString(3)));
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvType, tvDescription, tvDate;
        CardView cvReport;
        ImageView reportImage;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvType = itemView.findViewById(R.id.tvType);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvReportDate);
            cvReport = itemView.findViewById(R.id.cvReport);
            reportImage = itemView.findViewById(R.id.report_img);

        }
    }
}

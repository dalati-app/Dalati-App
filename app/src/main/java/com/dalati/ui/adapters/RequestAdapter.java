package com.dalati.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
import com.dalati.ui.models.Request;
import com.dalati.ui.models.Type;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ImageViewHolder> {

    Context mContext;
    List<Report> reportList;
    private Context context;
    List<Request> requestList = new ArrayList<>();
    int type = 0;
    List<Category> categoryList = new ArrayList<>();
    List<Type> typeList = new ArrayList<>();
    SQLiteDatabase db;
    public static final int ITEM_TYPE_GRID = 1;
    public static final int ITEM_TYPE_LIST = 0;
    private int VIEW_TYPE = 0;
    List<Report> tempReportList;
    String currentLanguage;
    Date date;
    int status;
    String statusMessage;

    private static final SimpleDateFormat dateFormatterNew = new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public RequestAdapter(Context context) {
        this.mContext = context;
        currentLanguage = Locale.getDefault().getLanguage();
        getCategory();
        getTypes();
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.request_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Request request = requestList.get(position);
        status = request.getStatus();


        for (Report reportObj : reportList) {
            Log.i("TAGi", "Request data " + reportObj.getPlace());
            Log.i("TAGi", "RequestId " + reportObj.getId() + "\n" + request.getReport_id());
            if (reportObj.getId().equals(request.getReport_id())) {
                if (reportObj.getImages().size() > 0) {

                    // convert url to image
                    Glide.with(mContext)
                            .load(reportObj.getImages().get(0))
                            .centerCrop()
                            .into(holder.reportImage);
                }
                date = null;
                try {
                    date = format.parse(reportObj.getPublishing_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (typeList != null && categoryList != null && reportList != null && requestList != null) {
                    for (Type type : typeList) {
                        if (type.getId().equals(reportObj.getType_id())) {
                            if (currentLanguage.equals("ar"))
                                holder.tvType.setText(type.getNameAr());
                            else
                                holder.tvType.setText(type.getNameEn());
                        }
                    }

                    for (Category category : categoryList) {
                        if (category.getId().equals(reportObj.getCategory_id())) {
                            if (currentLanguage.equals("ar"))
                                holder.tvCategory.setText(category.getNameAr());
                            else
                                holder.tvCategory.setText(category.getNameEn());
                        }
                    }
                }
                assert date != null;
                holder.tvDate.setText(String.valueOf(dateFormatterNew.format(date)));
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

                switch (status) {
                    case 0:
                        statusMessage = "Pending";

                        break;

                    case 1:
                        statusMessage = "Accepted";
                        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.green)));
                        break;

                    case 2:
                        statusMessage = "Declined";
                        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.red)));

                        break;
                    case 3:
                        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.orange)));
                        statusMessage = "Donated";
                        break;
                }
                holder.tvStatus.setText(statusMessage);

            }

        }

    }


    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;

    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
        notifyDataSetChanged();
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
        return requestList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvStatus, tvType, tvDescription, tvDate;
        CardView cvReport;
        ImageView reportImage;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvReportDate);
            cvReport = itemView.findViewById(R.id.cvReport);
            reportImage = itemView.findViewById(R.id.report_img);

        }
    }
}

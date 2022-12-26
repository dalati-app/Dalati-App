package com.dalati.ui.activities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.dalati.ui.models.Report;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class ReportDetailsActivity extends AppCompatActivity {
    ImageSlider imageSlider;
    List<SlideModel> slideModelList = new ArrayList<>();
    TextView tvCategory, tvType, tvDescription, tvDate, tvPlace;
    String category, type, description, date, place;
    Report report;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        defineViews();
        getReportData();
        getCategory();
        getType();

    }

    private void defineViews() {
        tvCategory = findViewById(R.id.tvCategory);
        tvType = findViewById(R.id.tvType);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvPlace = findViewById(R.id.tvPlace);
        imageSlider = findViewById(R.id.image_slider);

    }

    public void getCategory() {
        try {
            db = openOrCreateDatabase("local", Context.MODE_PRIVATE, null);


            Cursor c = db.rawQuery("select * from categories", null);

            while (c.moveToNext()) {
                System.out.println("Moha: " + c.getString(0));

                if (c.getString(0).equals(report.getCategory_id())) {
                    tvCategory.setText(c.getString(1));

                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getType() {
        try {
            db = openOrCreateDatabase("local", Context.MODE_PRIVATE, null);


            Cursor c = db.rawQuery("select * from types", null);

            while (c.moveToNext()) {
                System.out.println(c.getString(0) + "\n" + report.getType_id());
                System.out.println("==================");

                if (c.getString(0).equals(report.getType_id())) {
                    tvType.setText(c.getString(1));
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getReportData() {
        report = (Report) getIntent().getSerializableExtra("report");
        tvCategory.setText(report.getCategory_id());
        tvType.setText(report.getType_id());
        tvDescription.setText(report.getDescription());
        tvDate.setText(report.getDate());
        tvPlace.setText(report.getPlace());

        List<String> images = new ArrayList<>();
        images = report.getImages();
        for (int i = 0; i < images.size(); i++) {
            slideModelList.add(new SlideModel(images.get(i), ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(slideModelList);

    }
}
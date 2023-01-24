package com.dalati.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dalati.R;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Request;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReportDetailsActivity extends BaseActivity {
    ImageSlider imageSlider;
    List<SlideModel> slideModelList = new ArrayList<>();
    TextView tvCategory, tvType, tvDescription, tvDate, tvPlace;
    String category, type, description, date, place;
    Report report;
    SQLiteDatabase db;
    ImageButton btnChat;
    Button btnClaim;
    String currentLanguage = Locale.getDefault().getLanguage();
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViews();
        getReportData();
        getCategory();
        getType();

    }

    @Override
    public int defineLayout() {
        return R.layout.activity_report_details;
    }

    private void defineViews() {
        tvCategory = findViewById(R.id.tvCategory);
        tvType = findViewById(R.id.tvType);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvPlace = findViewById(R.id.tvPlace);
        btnChat = findViewById(R.id.btnChat);
        imageSlider = findViewById(R.id.image_slider);
        btnClaim = findViewById(R.id.btnClaim);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChattingActivity.class));
            }
        });

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(ReportDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Please check photos and read description carefully before claiming, wrong claims may attract penalties.")
                        .setConfirmText("Ok! Claim")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String id = databaseReference.push().getKey();
                                assert id != null;
                                Request request = new Request(id, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(), report.getId(), 0, "");
                                databaseReference.child("Requests").child(id).setValue(request);
                                sDialog.setTitleText("Request Sent")
                                        .setContentText("You can now chat with admin to check")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    public void getCategory() {
        try {
            db = openOrCreateDatabase("local", Context.MODE_PRIVATE, null);


            Cursor c = db.rawQuery("select * from categories", null);

            while (c.moveToNext()) {

                if (c.getString(0).equals(report.getCategory_id())) {
                    if (currentLanguage.equals("ar"))
                        tvCategory.setText(c.getString(1));
                    else
                        tvCategory.setText(c.getString(2));

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


                if (c.getString(0).equals(report.getType_id())) {
                    if (currentLanguage.equals("ar"))
                        tvType.setText(c.getString(1));
                    else
                        tvType.setText(c.getString(2));

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
package com.dalati.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.dalati.R;
import com.dalati.ui.adapters.ReportAdapter;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    SearchView searchView;
    List<Report> reportList;
    RecyclerView recycler_reports;

    ReportAdapter reportAdapter;
    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    LottieAnimationView lottieAnimationView;
    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViews();
        getCategories();

        SearchManager searchManager =
                (SearchManager) getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) findViewById(R.id.search_bar);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setQueryHint(getString(R.string.search_by));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                reportAdapter.filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (reportAdapter != null)
                    reportAdapter.filter(newText);
                if (newText.length() == 0) {
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    recycler_reports.setVisibility(View.INVISIBLE);
                } else {
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                    recycler_reports.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public int defineLayout() {
        return R.layout.activity_search;
    }

    private void defineViews() {
        searchView = findViewById(R.id.search_bar);
        recycler_reports = findViewById(R.id.reports_recycler);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        btnBack = findViewById(R.id.btnBack);

        recycler_reports.setHasFixedSize(true);
        recycler_reports.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        categoryList = new ArrayList<>();
        typeList = new ArrayList<>();
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
            }
        });

    }

    private void getCategories() {

        databaseReference.child("Reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reportList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Report report = postSnapshot.getValue(Report.class);

                        //TODO Change the view
                        assert report != null;
                        if (report.getReport_type() == 1) {
                            reportList.add(report);
                        }
                    }
                    reportAdapter.setReportList(reportList);
                    recycler_reports.setAdapter(reportAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
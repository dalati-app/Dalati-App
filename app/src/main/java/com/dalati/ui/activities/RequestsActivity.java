package com.dalati.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalati.R;
import com.dalati.ui.adapters.RequestAdapter;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Request;
import com.dalati.ui.models.Type;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    List<Report> reportList;
    List<Request> requestList;
    RecyclerView recycler_reports;
    ShimmerFrameLayout shimmerFrameLayout;
    RequestAdapter requestAdapter;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        defineViews();
        getData();
    }

    private void defineViews() {


        recycler_reports = findViewById(R.id.reports_recycler);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        shimmerFrameLayout.startShimmer();

        recycler_reports.setHasFixedSize(true);
        recycler_reports.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        categoryList = new ArrayList<>();
        typeList = new ArrayList<>();
        reportList = new ArrayList<>();
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(this);
    }

    private void getData() {
        databaseReference.child("Reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reportList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Report report = postSnapshot.getValue(Report.class);

                        //TODO Change the view
                        assert report != null;
                        reportList.add(report);
                    }
                    requestAdapter.setReportList(reportList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    requestList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Request request = postSnapshot.getValue(Request.class);

                        //TODO Change the view
                        assert request != null;
                        if (request.getUser_id().equals(firebaseUser.getUid())) {
                            requestList.add(request);
                        }
                    }
                    requestAdapter.setRequestList(requestList);
                    recycler_reports.setAdapter(requestAdapter);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recycler_reports.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
package com.dalati.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    List<Type> typeList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        databaseReference = FirebaseDatabase.getInstance().getReference();



    }
}
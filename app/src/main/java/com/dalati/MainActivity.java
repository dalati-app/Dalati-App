package com.dalati;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dalati.ui.fragments.AccountFragment;
import com.dalati.ui.fragments.ChatFragment;
import com.dalati.ui.fragments.ExploreFragment;
import com.dalati.ui.fragments.HomeFragment;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Type;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.nav_bottom);
        table_Creation();
        table_Creation2();
        saveToLocalDB();


        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_profile:
                        fragment = new AccountFragment();
                        break;

                    case R.id.nav_explore:
                        fragment = new ExploreFragment();
                        break;

                    case R.id.nav_chat:

                        fragment = new ChatFragment();

                        break;

                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                            .commit();
                } else {

                }

            }
        });
        bottomNav.setItemSelected(R.id.nav_home,true);

        //Hello Team
        //Hi
    }


    private void saveToLocalDB() {
        categoryList = new ArrayList<>();
        typeList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                addCategories(categoryList);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Types").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                            Type type = postSnapshot2.getValue(Type.class);
                            typeList.add(type);
                        }
                        addTypes(typeList);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void table_Creation() {
        db = openOrCreateDatabase("local", Context.MODE_PRIVATE, null);
        try {
            String SQl = "create table categories (id varchar2(30), name_en varchar2(30),name_ar varchar2(30))";
            db.execSQL(SQl);

            String SQl2 = "create table types (id varchar2(30), name_en varchar2(30),name_ar varchar2(30),category_id varchar2(30))";
            db.execSQL(SQl2);

        } catch (Exception e) {
            System.out.println("Errori:" + e.getMessage());
        }
    }


    public void table_Creation2() {
        db = openOrCreateDatabase("local", Context.MODE_PRIVATE, null);
        try {
            String SQl = "create table types (id varchar2(30), name_en varchar2(30),name_ar varchar2(30),category_id varchar2(30))";
            db.execSQL(SQl);


        } catch (Exception e) {
            System.out.println("Errori:" + e.getMessage());
        }
    }

    private void addCategories(List<Category> categoryList) {
        try {
            for (int i = 0; i < categoryList.size(); i++) {
                String Query = "Select * from categories where id LIKE '" + categoryList.get(i).getId() + "'";
                Cursor cursor = db.rawQuery(Query, null);
                if (cursor.getCount() <= 0) {

                    String SQL = "insert into categories values ('" + categoryList.get(i).getId() + "','" + categoryList.get(i).getNameEn() + "' , '" + categoryList.get(i).getNameAr() + "')";
                    db.execSQL(SQL);
                }

            }

        } catch (Exception e) {
            System.out.println("Errori: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


        }
    }

    private void addTypes(List<Type> typeList) {
        try {
            for (int i = 0; i < typeList.size(); i++) {
                String Query = "Select * from types where id LIKE '" + typeList.get(i).getId() + "'";
                Cursor cursor = db.rawQuery(Query, null);
                if (cursor.getCount() <= 0) {

                    String SQL = "insert into types values ('" + typeList.get(i).getId() + "','" + typeList.get(i).getNameEn() + "' , '" + typeList.get(i).getNameAr() + "', '" + typeList.get(i).getCategoryId() + "')";
                    db.execSQL(SQL);
                }

            }


        } catch (Exception e) {
            System.out.println("Errori d: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


        }
    }

}

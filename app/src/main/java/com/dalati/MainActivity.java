package com.dalati;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dalati.ui.activities.RequestsActivity;
import com.dalati.ui.activities.auth.LoginActivity;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.base.LanguageManager;
import com.dalati.ui.fragments.AccountFragment;
import com.dalati.ui.fragments.ChatFragment;
import com.dalati.ui.fragments.ExploreFragment;
import com.dalati.ui.fragments.HomeFragment;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Type;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, AccountFragment.OnLanguageSelectedListener {
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    SQLiteDatabase db;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    Dialog language_dialog;
    LanguageManager languageManager;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bottomNav = findViewById(R.id.nav_bottom);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        menuIcon = findViewById(R.id.menu_icon);
        languageManager = new LanguageManager(this);


        table_Creation();
        table_Creation2();
        saveToLocalDB();
        navigationDrawer();
        langDialog();

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
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
        bottomNav.setItemSelected(R.id.nav_home, true);

    }

    @Override
    public int defineLayout() {
        return R.layout.activity_main;
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

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        // navigationView.setCheckedItem(R.id.nav_home2);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);

                }
            }
        });


    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_language:
                language_dialog.show();
                break;

            case R.id.nav_requests:
                startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                break;

            case R.id.log_out:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    public void langDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.language_dialog, null);


//        language_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btn_yes = view.findViewById(R.id.btn_yes);
        Button btn_no = view.findViewById(R.id.btn_no);
        TextView ask = view.findViewById(R.id.ask_language);
        ask.setText(getString(R.string.ask_lanugage));
        btn_no.setText(getString(R.string.no));
        btn_yes.setText(getString(R.string.yes));
        languageManager = new LanguageManager(this);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("language", lang);
                myEdit.commit();

                setLocal(lang);*/

                String currentLanguage = languageManager.getLang();
                String newLanguage = "";
                if (currentLanguage.equals("en")) {
                    newLanguage = "ar";
                    ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                } else if (currentLanguage.equals("ar")) {
                    newLanguage = "en";
                    ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);
                }

                languageManager.updateResources(newLanguage);
                language_dialog.cancel();
                recreate();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_dialog.cancel();

            }
        });

        builder.setView(view);
        language_dialog = builder.create();

    }

    @Override
    public void onLanguageSelected(String language) {
        languageManager.updateResources(language);
        restartActivity();
    }

    public void restartActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}

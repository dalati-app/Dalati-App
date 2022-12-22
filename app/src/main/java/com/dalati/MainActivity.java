package com.dalati;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dalati.ui.fragments.AccountFragment;
import com.dalati.ui.fragments.ChatFragment;
import com.dalati.ui.fragments.ExploreFragment;
import com.dalati.ui.fragments.HomeFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.nav_bottom);

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

        //Hello Team
        //Hi
    }
}

package com.dalati.ui.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageManager languageManager = new LanguageManager(this);
        languageManager.updateResources(languageManager.getLang());
        String code = languageManager.getLang();
        if (code.equals("ar")) {

            ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);

        } else if (code.equals("en")) {

            ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);
        }
        System.out.println("your base language is:" + languageManager.getLang());

        setContentView(defineLayout());

    }

    public abstract int defineLayout();

}

/*
*
*     LanguageManager languageManager;
        languageManager = new LanguageManager(this);
                languageManager.updateResources(selectedLanguage);

 * */
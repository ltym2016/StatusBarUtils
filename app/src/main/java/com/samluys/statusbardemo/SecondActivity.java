package com.samluys.statusbardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.samluys.statusbar.StatusBarUtils;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        StatusBarUtils.transparencyBar(this, false);
        StatusBarUtils.StatusBarIconDark(this);
    }
}

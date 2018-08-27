package com.samluys.statusbardemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.samluys.statusbar.StatusBarUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtils.transparencyBar(this, Color.parseColor("#15bfff"));

        Button btn_black = findViewById(R.id.btn_black);
        Button btn_white = findViewById(R.id.btn_white);

        btn_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusBarUtils.StatusBarIconDark(MainActivity.this);
            }
        });

        btn_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusBarUtils.StatusBarIconLight(MainActivity.this);
            }
        });

    }
}

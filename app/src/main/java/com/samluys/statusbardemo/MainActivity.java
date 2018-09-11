package com.samluys.statusbardemo;

import android.content.Intent;
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
        StatusBarUtils.transparencyBar(this, false);
        StatusBarUtils.StatusBarIconDark(this);

        Button btn_black = findViewById(R.id.btn_black);
        Button btn_white = findViewById(R.id.btn_white);
        Button btn_jump = findViewById(R.id.btn_jump);

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

        btn_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });



    }
}

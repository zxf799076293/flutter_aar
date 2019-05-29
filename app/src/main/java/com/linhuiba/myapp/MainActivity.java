package com.linhuiba.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.linhuiba.mylibrary.Config;

//import android.widget.FrameLayout;
//
//import io.flutter.facade.Flutter;
//import io.flutter.view.FlutterView;

public class MainActivity extends AppCompatActivity {
    private TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.textview);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, Config.TESTCONFIG, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,FlutterMainActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.ir_sj.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Splashscreen extends AppCompatActivity {

    Handler handle;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar=findViewById(R.id.pbar);
        progressBar.getProgress();
        handle=new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splashscreen.this,MainActivity.class));
                finish();
            }
        },2000);
    }
}


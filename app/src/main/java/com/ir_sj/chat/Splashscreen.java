package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {

    Handler handle;
    ProgressBar progressBar;
    static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBar=findViewById(R.id.pbar);
        progressBar.getProgress();
        handle=new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user == null) {
                    FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                Toast.makeText(Splashscreen.this, "Ready to roll..!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Splashscreen.this, RegisterActivity.class));
                            }
                        }
                    });
                }
                else
                    startActivity(new Intent(Splashscreen.this, MainActivity.class));
                finish();
            }
        },2000);
    }
}


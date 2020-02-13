package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirstPage extends AppCompatActivity {
    Button register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        register_btn = (Button)findViewById(R.id.register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(FirstPage.this, "Registering....", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //user = FirebaseAuth.getInstance().getCurrentUser();
                                startActivity(new Intent(FirstPage.this, RegisterActivity.class));
                                Toast.makeText(FirstPage.this, "Yayyy...Just one more step to go..!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
    }

    public void register(View v)
    {
        Toast.makeText(FirstPage.this, "Registering....", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //user = FirebaseAuth.getInstance().getCurrentUser();
                    startActivity(new Intent(FirstPage.this, RegisterActivity.class));
                    Toast.makeText(FirstPage.this, "Ready to roll..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

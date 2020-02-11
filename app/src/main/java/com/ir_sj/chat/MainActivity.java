package com.ir_sj.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    int REGISTER_CODE = 1;
    private ImageButton AddNewPostButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToPostactivity();
            }
        });


        if(FirebaseAuth.getInstance().getCurrentUser() == null) //no user signed in
        {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivityForResult(i, REGISTER_CODE);
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendUserToPostactivity()
    {
        Intent addNewPostIntent= new Intent(MainActivity.this,postactivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }

        if(item.getItemId() == R.id.menu_change_mode)
        {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REGISTER_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show();
                //displayChat();
            }
            else
            {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
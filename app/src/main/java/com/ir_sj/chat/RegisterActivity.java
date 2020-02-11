package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity
{
    EditText name;
    Button chooseImage;
    ImageButton start;
    ImageView dp;
    String dp_name, uid;
    Uri img;
    int RESULT_LOAD_IMAGE=1;
    //FirebaseUser user;
    DatabaseReference ref;
    StorageReference sref;
    String downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //this.getSupportActionBar().hide();    //to hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.name);
        chooseImage = (Button)findViewById(R.id.choose_image);
        start = (ImageButton)findViewById(R.id.start);
        dp = (ImageView)findViewById(R.id.imageView);

        //user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            Toast.makeText(RegisterActivity.this, "bad",Toast.LENGTH_SHORT);

        ref = FirebaseDatabase.getInstance().getReference("UserData");
        sref = FirebaseStorage.getInstance().getReference().child("profile_images/" + uid);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String dname = name.getText().toString();

                HashMap<Object, String> hashMap = new HashMap<>();

                hashMap.put("uid", uid);
                hashMap.put("name", dname);
                hashMap.put("image", downloadUri);

                ref.child(uid).setValue(hashMap);
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data && data.getData() != null)
        {
            img = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), img);
                dp.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage()
    {
        if(img != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            /*sref.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                    downloadUri = task
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });*/
            sref.putFile(img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                        downloadUri = task.getResult().getStorage().getDownloadUrl().toString();

                    }
                }
            });
        }
    }
}
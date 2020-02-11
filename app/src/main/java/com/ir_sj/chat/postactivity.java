package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class postactivity extends AppCompatActivity {

    private ImageButton Selectpostimage;
    private Button Updatepostbutton;
    private EditText Postdescription;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;
    private String Description;
    private StorageReference Postimagesreference;
    private String saveCurrentDate,saveCurrentTime,postRandomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postactivity);

        Postimagesreference= FirebaseStorage.getInstance().getReference();

        Selectpostimage=(ImageButton) findViewById(R.id.select_post_image);
        Updatepostbutton=(Button) findViewById(R.id.update_button);
        Postdescription=(EditText) findViewById(R.id.post_description);

        Selectpostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        Updatepostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validatepostinfo();
            }
        });
    }



    private void Validatepostinfo()
    {
        Description=Postdescription.getText().toString();
        if(ImageUri==null)
        {
            Toast.makeText(this,"Please select image",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Kindly insert a caption",Toast.LENGTH_SHORT).show();
        }
        else
        {
            StroingImagetoFirebaseStorage();
        }
    }

    private void StroingImagetoFirebaseStorage()
    {
        Calendar calFordDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate= currentDate.format(calFordDate.getTime());

        Calendar calFordTime= Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime= currentTime.format(calFordTime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        StorageReference filePath= Postimagesreference.child("Post Images").child(ImageUri.getLastPathSegment()+ postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(postactivity.this,"image uploaded successfully",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    String message=task.getException().getMessage();
                    Toast.makeText(postactivity.this,"error occured" + message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            Selectpostimage.setImageURI(ImageUri);
        }
    }

    private void OpenGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }
}


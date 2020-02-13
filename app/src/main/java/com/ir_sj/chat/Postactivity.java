package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Postactivity extends AppCompatActivity {

    private ImageButton Selectpostimage;
    private Button Updatepostbutton;
    private ProgressDialog loadingBar;
    private EditText Postdescription;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;
    private String Description;
    private StorageReference Postimagesreference;
    private DatabaseReference UsersRef,PostsRef;
    private String saveCurrentDate,saveCurrentTime,postRandomName,downloadurl,current_user_id;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postactivity);

        mAuth= FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        Postimagesreference= FirebaseStorage.getInstance().getReference();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("UserData");
        PostsRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        Selectpostimage=(ImageButton) findViewById(R.id.select_post_image);
        Updatepostbutton=(Button) findViewById(R.id.update_button);
        Postdescription=(EditText) findViewById(R.id.post_description);
        loadingBar=new ProgressDialog(this);

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
            loadingBar.setTitle("ADD new post");
            loadingBar.setMessage("wait.....updating !!!");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
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

                    downloadurl= task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(Postactivity.this,"image uploaded successfully",Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();

                }
                else
                {
                    String message=task.getException().getMessage();
                    Toast.makeText(Postactivity.this,"error occured" + message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SavingPostInformationToDatabase()
    {
       UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(dataSnapshot.exists())
               {
                   String UserFullName=dataSnapshot.child("name").getValue().toString();
                   String UserProfileImage=dataSnapshot.child("image").getValue().toString();

                   HashMap postsMap=new HashMap();
                   postsMap.put("uid",current_user_id);
                   postsMap.put("date",saveCurrentDate);
                   postsMap.put("time",saveCurrentTime);
                   postsMap.put("description",Description);
                   postsMap.put("postimage",downloadurl);
                   postsMap.put("dp",UserProfileImage);
                   postsMap.put("name",UserFullName);
                 PostsRef.child(current_user_id+postRandomName).updateChildren(postsMap)
                 .addOnCompleteListener(new OnCompleteListener() {
                     @Override
                     public void onComplete(@NonNull Task task)
                     {
                         if(task.isSuccessful())
                         {
                             Toast.makeText(Postactivity.this,"new post is updated sucessfully",Toast.LENGTH_SHORT).show();
                             loadingBar.dismiss();
                             SendUserToMainActivity();
                         }
                         else
                         {
                             Toast.makeText(Postactivity.this,"error occured",Toast.LENGTH_SHORT).show();
                             loadingBar.dismiss();
                         }
                     }

                 });

               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void SendUserToMainActivity()
    {
        Intent i =  new Intent(Postactivity.this, MainActivity.class);
        startActivity(i);
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


package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
//import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    int REGISTER_CODE = 1;
    private ImageButton AddNewPostButton;
    TextView userName;
    CircularImageView imgview;
    //FirebaseUser user;
    DatabaseReference ref,PostsRef;
    StorageReference sref;
    String uid;
    private RecyclerView postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PostsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToPostactivity();
            }
        });


        postList= (RecyclerView)findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager LinearLayoutManager=new LinearLayoutManager(this);
        LinearLayoutManager.setReverseLayout(true);
        LinearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(LinearLayoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        userName = (TextView)findViewById(R.id.userName);
        imgview = (CircularImageView)findViewById(R.id.image);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);


            //user = FirebaseAuth.getInstance().getCurrentUser();
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference("UserData/"+uid);
            sref = FirebaseStorage.getInstance().getReference("profile_images/");
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();

            ref.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    //Toast.makeText(MainActivity.this, value, Toast.LENGTH_LONG).show();
                    userName.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            ref.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);

                    // imgview.setImageURI(Uri.parse(value));
                    sref.child(uid).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL
                                    //Glide.with(getApplicationContext()).load(uri).into(imgview);

                                    //Picasso.get().load(uri).into(imgview);
                      Picasso.get().load(uri).fit().centerCrop().into(imgview);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            DisplayAllUsersPosts();
        }

        private void DisplayAllUsersPosts()
        {
           /* FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Posts,PostsViewHolder>
                            (
                                    Posts.class,
                                    R.layout.all_posts_layout,
                                    PostsViewHolder.class,
                                    PostsRef
                            )
                    {
                        @Override
                        protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i, @NonNull Posts posts) {

                        }

                        @Override
                        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
                            super.onBindViewHolder(holder, position);
                        }

                        @NonNull
                        @Override
                        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return null;
                        }

                        protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
                        {
                            viewHolder.setFullname(model.getFullname());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setDate(model.getDate());
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                            viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                        }

`j
                    };*/
            FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostsRef, Posts.class).build();
           FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                   (options) {
               @Override
               protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i, @NonNull Posts posts) {

               }

               @NonNull
               @Override
               public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                   View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);

                   return new PostsViewHolder(view);
               }
           };
            postList.setAdapter(firebaseRecyclerAdapter);
        }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1,  String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).into(PostImage);
        }



    }



    private void SendUserToPostactivity()
    {
        Intent addNewPostIntent= new Intent(MainActivity.this, Postactivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Do you want to sign out ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(MainActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                                            Intent signedout = new Intent(MainActivity.this, Splashscreen.class);
                                            startActivity(signedout);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Sign Out?");
                            alert.show();
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
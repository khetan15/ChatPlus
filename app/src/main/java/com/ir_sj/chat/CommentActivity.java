package com.ir_sj.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;

    private DatabaseReference UserRef, PostsRef;
    private FirebaseAuth mAuth;

    //private  FirebaseRecyclerAdapter firebaseRecyclerAdapterC;
    private String Post_Key, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Toast.makeText(this, Post_Key, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference("UserData");
        PostsRef= FirebaseDatabase.getInstance().getReference("Posts/"+Post_Key+"/Comments");

        Toast.makeText(this, "oncreate"+Post_Key, Toast.LENGTH_SHORT).show();

        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_bin); //btn ko bin likh diya hai galti se

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("name").getValue().toString();

                            ValidateComment(userName);
                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        DisplayComments();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Post_Key = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference("UserData");
        PostsRef= FirebaseDatabase.getInstance().getReference("Posts/"+Post_Key+"/Comments");

        //Toast.makeText(this, "onstart"+Post_Key, Toast.LENGTH_SHORT).show();
        DisplayComments();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Post_Key = getIntent().getExtras().get("PostKey").toString();
        UserRef = FirebaseDatabase.getInstance().getReference("UserData");
        PostsRef= FirebaseDatabase.getInstance().getReference("Posts/"+Post_Key+"/Comments");

        //Toast.makeText(this, "onresume"+Post_Key, Toast.LENGTH_SHORT).show();
        DisplayComments();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(PostsRef, Comments.class).build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapterC = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder commentsViewHolder, int i, @NonNull final Comments comments) {
                commentsViewHolder.myUserName.setText(comments.getUsername());
                commentsViewHolder.myTime.setText(comments.getTime());
                commentsViewHolder.myDate.setText(comments.getDate());
                commentsViewHolder.myComment.setText(comments.getComment());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);

                return new CommentsViewHolder(view);
            }
        };
        CommentsList.setAdapter(firebaseRecyclerAdapterC);
        firebaseRecyclerAdapterC.startListening();
    }*/

    private void DisplayComments()
    {
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(PostsRef, Comments.class).build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapterC = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder commentsViewHolder, int i, @NonNull final Comments comments) {
                commentsViewHolder.myUserName.setText(comments.getUsername());
                commentsViewHolder.myTime.setText(comments.getTime());
                commentsViewHolder.myDate.setText(comments.getDate());
                commentsViewHolder.myComment.setText(comments.getComment());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);

                return new CommentsViewHolder(view);
            }
        };
        CommentsList.setAdapter(firebaseRecyclerAdapterC);
        firebaseRecyclerAdapterC.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        TextView myUserName, myComment, myDate, myTime;
        public CommentsViewHolder(@NonNull View mView)
        {
            super(mView);
            myUserName = (TextView)mView.findViewById(R.id.comment_username);
            myComment = (TextView)mView.findViewById(R.id.comment_text);
            myDate = (TextView)mView.findViewById(R.id.comment_date);
            myTime = (TextView)mView.findViewById(R.id.comment_time);
        }
    }

    private void ValidateComment(String userName) {
        String commentText = CommentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please write text to comment..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCuerrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCuerrentTime = currentTime.format(calForDate.getTime());

            final String RandomKey = current_user_id + saveCuerrentDate + saveCuerrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",current_user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date", saveCuerrentDate);
            commentsMap.put("time",saveCuerrentTime);
            commentsMap.put("username",userName);

            PostsRef.push().setValue(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(CommentActivity.this, "You have commented successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CommentActivity.this, "Error occured, try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}







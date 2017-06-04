package com.example.ganesh.story.activeStory.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ganesh.story.R;
import com.example.ganesh.story.model.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentActivity extends AppCompatActivity {

    private static final String LOG_TAG = CommentActivity.class.getName();
    private static DatabaseReference mStoryComment;
    FirebaseAuth mAuth;
    ImageButton mImageButtonDeleteComment;
    private RecyclerView mCommentRecylerview;
    private DatabaseReference mDatabaseComment;
    private EditText mEditTextComment;
    private ImageButton mImageButtonComment;
    private String mUserStoryComment;
    private String story_key, current_user;
    private DatabaseReference mDatabaseStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initializeScreen();

        try {
            Intent intent = getIntent();
            story_key = intent.getStringExtra("Story_key");
            Log.e(LOG_TAG, "story_key:" + story_key);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "NullPoinerException");
        }

        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("comment").child(story_key);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseComment.keepSynced(true);

        mStoryComment = FirebaseDatabase.getInstance().getReference().child("Story_that_have_comment");
        mStoryComment.keepSynced(true);

        mDatabaseStory = FirebaseDatabase.getInstance().getReference().child("story");
        mDatabaseStory.keepSynced(true);

        current_user = mAuth.getCurrentUser().getUid();

        mImageButtonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        mDatabaseStory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_story) {

                String story_username = String.valueOf(dataSnapshot_story.child(story_key).child("username").getValue());
                final String story_title = String.valueOf(dataSnapshot_story.child(story_key).child("title").getValue());

                mStoryComment.child(story_key).child("username").setValue(story_username);
                mStoryComment.child(story_key).child("title").setValue(story_title);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "**********");
            }
        });


        mStoryComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String username = String.valueOf(dataSnapshot.child(story_key).child("username").getValue());
                Log.e(LOG_TAG, "username:" + username);
                TextView story_comment_username = (TextView) findViewById(R.id.text_view_comment_username);
                story_comment_username.setText(username);

                String title = String.valueOf(dataSnapshot.child(story_key).child("title").getValue());
                Log.e(LOG_TAG, "title:" + title);
                TextView story_comment_title = (TextView) findViewById(R.id.text_view_comment_title);
                story_comment_title.setText(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "error while retriving story comment username title");
            }
        });


    }

    private void addComment() {
        mUserStoryComment = mEditTextComment.getText().toString();


        if (!TextUtils.isEmpty(mUserStoryComment)) {

            final DatabaseReference mDatabaseNewCommentRef = mDatabaseComment.push();

            mDatabaseNewCommentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot_comment) {

                    final DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot_users) {
                            String story_username = String.valueOf(dataSnapshot_users.child(current_user).child("username").getValue());

                            mDatabaseNewCommentRef.child("username").setValue(story_username);
                            mDatabaseNewCommentRef.child("comment").setValue(mUserStoryComment);


                            mEditTextComment.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, "failed to get username1");
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(LOG_TAG, "failed to add comment!");
                }
            });


        }

    }


    private void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mCommentRecylerview = (RecyclerView) findViewById(R.id.recycler_view_comment);
        mCommentRecylerview.setHasFixedSize(true);
        mCommentRecylerview.setLayoutManager(new LinearLayoutManager(this));


        mEditTextComment = (EditText) findViewById(R.id.edit_text_new_comment);
        mImageButtonComment = (ImageButton) findViewById(R.id.image_buuton_comment);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comment, StoryCommentHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, StoryCommentHolder>(
                Comment.class,
                R.layout.single_comment_layout,
                StoryCommentHolder.class,
                mDatabaseComment
        ) {
            @Override
            protected void populateViewHolder(final StoryCommentHolder viewHolder, Comment model, int position) {

                final String story_comment_key = getRef(position).getKey();

                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());

                if (viewHolder.mImageButtonDeleteComment.getVisibility() == View.INVISIBLE) {

                    viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            viewHolder.mImageButtonDeleteComment.setVisibility(View.VISIBLE);

                            if (viewHolder.mImageButtonDeleteComment.getVisibility() == View.VISIBLE) {
                                viewHolder.mImageButtonDeleteComment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDatabaseComment.child(story_comment_key).removeValue();
                                    }
                                });
                            }
                            return true;
                        }
                    });

                }

            }


        };

        mCommentRecylerview.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static class StoryCommentHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mImageButtonDeleteComment;

        public StoryCommentHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImageButtonDeleteComment = (ImageButton) mView.findViewById(R.id.image_button_delete_comment);
            mImageButtonDeleteComment.setVisibility(View.INVISIBLE);
        }

        public void setUsername(String username) {

            TextView story_comment_username = (TextView) mView.findViewById(R.id._single_text_view_comment_username);
            story_comment_username.setText(username);

        }

        public void setComment(String comment) {
            TextView story_comment = (TextView) mView.findViewById(R.id.single_text_view_comment_title);
            story_comment.setText(comment);

        }


    }


}

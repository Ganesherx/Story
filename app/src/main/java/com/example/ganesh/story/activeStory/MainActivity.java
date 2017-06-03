package com.example.ganesh.story.activeStory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ganesh.story.R;
import com.example.ganesh.story.model.Story;
import com.example.ganesh.story.ui.authentication.LoginActivity;
import com.example.ganesh.story.ui.like.StoryLikeActivty;
import com.example.ganesh.story.ui.post.PostActivity;
import com.example.ganesh.story.ui.profile.ProfileActivity;
import com.example.ganesh.story.ui.search.SearchActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getName();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView mStoryRecylerview;

    private boolean isStoryLike = false;
    private DatabaseReference mDatasbeStoryLike;


    private boolean isStoryBookmark = false;
    private DatabaseReference mDatabaseStoryBookmark;
    private DatabaseReference mDatabaseUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeScrren();


        //DATABASE CONNECTION
        mDatabase = FirebaseDatabase.getInstance().getReference().child("story");
        mDatasbeStoryLike = FirebaseDatabase.getInstance().getReference().child("likes");
        mDatabaseStoryBookmark = FirebaseDatabase.getInstance().getReference().child("bookmark");
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("users");


        mDatabase.keepSynced(true);
        mDatasbeStoryLike.keepSynced(true);
        mDatabaseStoryBookmark.keepSynced(true);


        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        try {
            String current_user = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception");
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.e(LOG_TAG, "current user:" + (firebaseAuth.getCurrentUser()));
                    Intent createAccountIntent = new Intent(MainActivity.this, LoginActivity.class);
                    createAccountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(createAccountIntent);
                    finish();
                }
            }
        };

        //Bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                break;
                            case R.id.action_like:
                                startActivity(new Intent(MainActivity.this, StoryLikeActivty.class));
                                break;
                            case R.id.action_search:
                                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                                break;
                            case R.id.action_profile:
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;
                        }
                        return false;
                    }
                });


        mStoryRecylerview = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        mStoryRecylerview.setHasFixedSize(true);
        mStoryRecylerview.setLayoutManager(new LinearLayoutManager(this));

    }


    private void initializeScrren() {
        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Story");
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Story, StoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>(
                Story.class,
                R.layout.single_story_layout,
                StoryViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final StoryViewHolder viewHolder, Story model, final int position) {

                final String story_key = getRef(position).getKey();
                Log.e(LOG_TAG, "story_key:" + story_key);
                final String current_user = mAuth.getCurrentUser().getUid();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());


                //ONCLICKLISTENER FOR SINGLE STORY (GETTING KEY OF STORY FROM FIREBASE)
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "" + story_key, Toast.LENGTH_SHORT).show();
                    }
                });

                //ADDING STORY LIKE IN FIREBASE
                viewHolder.setStoryLikeButton(story_key);


                viewHolder.mImageButtonStoryLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStoryLike = true;


                        mDatasbeStoryLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (isStoryLike) {

                                    if (dataSnapshot.child(story_key).hasChild(current_user)) {
                                        //REMOVE LIKE FROMTHE STORY
                                        mDatasbeStoryLike.child(story_key).child(current_user).removeValue();
                                        isStoryLike = false;

                                    } else {
                                        //LIKE THE STORY

                                        mDatabaseUserRef.child(current_user).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot_user) {
                                                String bookmark_username = String.valueOf(dataSnapshot_user.child("username").getValue());
                                                mDatasbeStoryLike.child(story_key).child(current_user).setValue(bookmark_username);
                                                isStoryLike = false;
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }//ELSE
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(LOG_TAG, "Error while adding like to this story!:" + databaseError);
                            }
                        });


                    }
                });


                //ADDING STORY BOOKMARK IN FIREBASE

                viewHolder.setStoryBookmark(story_key);
                viewHolder.mImageButtonStoryBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStoryBookmark = true;

                        mDatabaseStoryBookmark.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (isStoryBookmark) {

                                    if (dataSnapshot.child(current_user).hasChild(story_key)) {
                                        //REMOVE BOOKMARK FROM THIS STORY
                                        mDatabaseStoryBookmark.child(current_user).child(story_key).removeValue();
                                        isStoryBookmark = false;


                                    } else {

                                        mDatabase.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                String bookmark_username = String.valueOf(dataSnapshot.child(story_key).child("username").getValue());
                                                String bookmark_title = String.valueOf(dataSnapshot.child(story_key).child("title").getValue());
                                                String bookmark_image = String.valueOf(dataSnapshot.child(story_key).child("image").getValue());

                                                mDatabaseStoryBookmark.child(current_user).child(story_key).child("username").setValue(bookmark_username);
                                                mDatabaseStoryBookmark.child(current_user).child(story_key).child("title").setValue(bookmark_title);
                                                mDatabaseStoryBookmark.child(current_user).child(story_key).child("image").setValue(bookmark_image);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e(LOG_TAG, "failed ");
                                            }
                                        });

                                        isStoryBookmark = false;
                                        Toast.makeText(MainActivity.this, "Story bookmarked", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(LOG_TAG, "Error while adding bookmark for this story!:" + databaseError);
                            }
                        });

                    }
                });

            }
        };

        mStoryRecylerview.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                Toast.makeText(MainActivity.this, "Notification", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void postNewStory(View view) {
        startActivity(new Intent(MainActivity.this, PostActivity.class));
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        View mView;

        //STROY LIKE BUTTON
        DatabaseReference mDatabaseStoryLike;
        FirebaseAuth mAuthStoryLike;
        //STORY BOOKMARK
        DatabaseReference mDatabaseStoryBookmark;
        FirebaseAuth mAuthStoryBookmark;
        //STORY LIKE COUNT
        DatabaseReference mDatabaseAddStroyLikeCountRef;
        FirebaseAuth mAuthStoryLikeCount;
        private ImageButton mImageButtonStoryLike;
        private ImageButton mImageButtonStoryBookmark;


        public StoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            //TO CHANEG THE COLOR OF STORY LIKE BUTTON
            mImageButtonStoryLike = (ImageButton) mView.findViewById(R.id.image_button_story_like);
            mDatabaseStoryLike = FirebaseDatabase.getInstance().getReference().child("likes");
            mAuthStoryLike = FirebaseAuth.getInstance();
            mDatabaseStoryLike.keepSynced(true);

            //BOOKMARK
            mImageButtonStoryBookmark = (ImageButton) mView.findViewById(R.id.image_button_story_bookmark);
            mDatabaseStoryBookmark = FirebaseDatabase.getInstance().getReference().child("bookmark");
            mAuthStoryBookmark = FirebaseAuth.getInstance();
            mDatabaseStoryBookmark.keepSynced(true);

            //STORY LKE COUNT
            mDatabaseAddStroyLikeCountRef = FirebaseDatabase.getInstance().getReference().child("story_count");
            mAuthStoryLikeCount = FirebaseAuth.getInstance();
            mDatabaseAddStroyLikeCountRef.keepSynced(true);


        }

        //TO CHANEG THE COLOR OF STORY LIKE BUTTON
        public void setStoryLikeButton(final String story_key) {

            mDatabaseStoryLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(story_key).hasChild(mAuthStoryLike.getCurrentUser().getUid())) {
                        mImageButtonStoryLike.setImageResource(R.drawable.like_icon_dark);

                    } else {
                        mImageButtonStoryLike.setImageResource(R.drawable.story_like_icon);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        //TO CHANGE THE COLOR OF STORY BOOKMARK BUTTON
        public void setStoryBookmark(final String story_key) {
            mDatabaseStoryBookmark.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(mAuthStoryBookmark.getCurrentUser().getUid()).hasChild(story_key)) {
                        mImageButtonStoryBookmark.setImageResource(R.drawable.story_bookmark_dark);

                    } else {
                        mImageButtonStoryBookmark.setImageResource(R.drawable.story_bookmark);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title) {
            TextView story_title = (TextView) mView.findViewById(R.id.text_view_story_title);
            story_title.setText(title);
        }

        public void setUsername(String username) {
            TextView story_title_username = (TextView) mView.findViewById(R.id.text_view_story_username_for_title);
            story_title_username.setText(username);

            TextView story_username = (TextView) mView.findViewById(R.id.text_view_story_user_name);
            story_username.setText(username);

        }

        public void setImage(Context context, String image) {
            ImageView story_image = (ImageView) mView.findViewById(R.id.image_view_story_image);
            Picasso.with(context).load(image).into(story_image);
        }


    }
}

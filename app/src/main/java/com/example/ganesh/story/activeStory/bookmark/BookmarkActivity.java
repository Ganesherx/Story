package com.example.ganesh.story.activeStory.bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ganesh.story.R;
import com.example.ganesh.story.model.bookmark;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BookmarkActivity extends AppCompatActivity {
    private final String LOG_TAG = BookmarkActivity.class.getName();

    private DatabaseReference mDatabaseBookmark;
    private FirebaseAuth mAuth;

    private RecyclerView mStoryBookmarkRecylerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        initializeScrren();
        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        final String current_user = mAuth.getCurrentUser().getUid();


        //DATABASE CONNECTION
        mDatabaseBookmark = FirebaseDatabase.getInstance().getReference().child("bookmark").child(current_user);
        Log.e(LOG_TAG, "Database:" + mDatabaseBookmark);

        mDatabaseBookmark.keepSynced(true);

        mStoryBookmarkRecylerview = (RecyclerView) findViewById(R.id.story_bookmark_recylerview);
        mStoryBookmarkRecylerview.setHasFixedSize(true);
        mStoryBookmarkRecylerview.setLayoutManager(new LinearLayoutManager(this));


    }

    private void initializeScrren() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<bookmark, StoryBookmarkHolder> firebaseRecyclerHolder = new FirebaseRecyclerAdapter<bookmark, StoryBookmarkHolder>(
                bookmark.class,
                R.layout.single_bookmark_layout,
                StoryBookmarkHolder.class,
                mDatabaseBookmark
        ) {
            @Override
            protected void populateViewHolder(StoryBookmarkHolder viewHolder, bookmark model, final int position) {

                final String story_key = getRef(position).getKey();
                Log.e(LOG_TAG, "story_key:" + story_key);
                final String current_user = mAuth.getCurrentUser().getUid();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                //REMOVE BOOKMARK
                viewHolder.mImageButtonBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookmarkActivity.this)
                                .setTitle(BookmarkActivity.this.getString(R.string.remove_item_option))
                                .setMessage(BookmarkActivity.this.getString(R.string.dialog_message_are_you_sure_remove_bookmark))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        //REMOVING BOOKMARK

                                        mDatabaseBookmark.child(story_key).removeValue();

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                                    /* Dismiss the dialog */
                                        dialog.dismiss();
                                    }
                                });


                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                });

            }
        };

        mStoryBookmarkRecylerview.setAdapter(firebaseRecyclerHolder);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static class StoryBookmarkHolder extends RecyclerView.ViewHolder {

        protected ImageButton mImageButtonBookmark;
        View mView;

        public StoryBookmarkHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImageButtonBookmark = (ImageButton) mView.findViewById(R.id.image_button_story_bookmark);
        }

        public void setTitle(String title) {

            TextView story_title = (TextView) mView.findViewById(R.id.text_view_bookmark_title);
            story_title.setText(title);
        }

        public void setUsername(String username) {

            TextView story_title_username = (TextView) mView.findViewById(R.id.text_view_bookmark_username);
            story_title_username.setText(username);

        }

        public void setImage(Context context, String image) {
            ImageView story_image = (ImageView) mView.findViewById(R.id.image_view_bookmark_image);
            Picasso.with(context).load(image).into(story_image);
        }


    }
}

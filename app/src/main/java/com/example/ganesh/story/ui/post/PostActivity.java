package com.example.ganesh.story.ui.post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ganesh.story.R;
import com.example.ganesh.story.activeStory.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final String LOG_TAG = PostActivity.class.getName();
    private static final int GALLERY_REQUEST = 1;
    private ImageView imageView;
    private EditText mEditTextTitle;
    private TextView mTextViewShare;
    private String mUserStoryTitle;
    private Uri imageUri;
    private RelativeLayout mRelativeLayout;
    private ProgressDialog mAuthProgessDialog;


    private DatabaseReference mDatabase;
    private StorageReference mStoragereference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initializeScreen();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_REQUEST);
            }
        });

        mTextViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMyStory();
            }
        });


    }

    private void postMyStory() {

        mAuthProgessDialog.show();
        mUserStoryTitle = mEditTextTitle.getText().toString();
        if (!TextUtils.isEmpty(mUserStoryTitle) && imageUri != null) {
            StorageReference filePath = mStoragereference.child("story_image").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri dowanlodUri = taskSnapshot.getDownloadUrl();

                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference newStoryReference = mDatabase.child("story");
                    final DatabaseReference storyLocation = newStoryReference.push();


                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            storyLocation.child("title").setValue(mUserStoryTitle);
                            storyLocation.child("image").setValue(dowanlodUri.toString());
                            storyLocation.child("uid").setValue(mCurrentUser.getUid());
                            storyLocation.child("username").setValue(dataSnapshot.child("username").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                mAuthProgessDialog.dismiss();
                                                Snackbar snackbar = Snackbar.make(mRelativeLayout, "Story uploded successfully", Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                                mEditTextTitle.setText("");

                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, "Error while retriving username for stroy!");
                        }
                    });

                    startActivity(new Intent(PostActivity.this, MainActivity.class));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, "Uploding failed");
                }
            });
        }

    }

    private void initializeScreen() {
        imageView = (ImageView) findViewById(R.id.image_view_choose_image);
        mEditTextTitle = (EditText) findViewById(R.id.edit_text_title_for_story);
        mTextViewShare = (TextView) findViewById(R.id.text_view_share_my_story);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);

        mStoragereference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());

        mAuthProgessDialog = new ProgressDialog(this);
        mAuthProgessDialog.setCancelable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
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
        return super.onOptionsItemSelected(item);
    }


    public void closePost(View view) {
        finish();
    }
}

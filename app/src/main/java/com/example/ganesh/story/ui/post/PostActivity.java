package com.example.ganesh.story.ui.post;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


    private DatabaseReference mDatabase;
    private StorageReference mStoragereference;
    private FirebaseAuth mAuth;


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

        mUserStoryTitle = mEditTextTitle.getText().toString();
        if (!TextUtils.isEmpty(mUserStoryTitle) && imageUri != null) {
            StorageReference filePath = mStoragereference.child("story_image").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri dowanlodUri = taskSnapshot.getDownloadUrl();

                    String user_id=mAuth.getCurrentUser().getUid();
                    DatabaseReference newStoryReference=mDatabase.child(user_id).push();


                    newStoryReference.child("title").setValue(mUserStoryTitle);
                    newStoryReference.child("image").setValue(dowanlodUri.toString());




                    Snackbar snackbar = Snackbar.make(mRelativeLayout, "Story uploded successfully", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    mEditTextTitle.setText("");


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
        mDatabase= FirebaseDatabase.getInstance().getReference().child("story");
        mAuth = FirebaseAuth.getInstance();


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

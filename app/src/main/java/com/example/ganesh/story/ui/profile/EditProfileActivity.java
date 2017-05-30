package com.example.ganesh.story.ui.profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ganesh.story.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = EditProfileActivity.class.getName();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView mUsername, mUserEmail, mName;
    private EditText mEditTextBio;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeScreen();


        final String user_id = mAuth.getCurrentUser().getUid();
        Log.e(LOG_TAG, "user_id:" + user_id);
        DatabaseReference userReference = mDatabase.child(user_id);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("username").getValue();
                String useremail = (String) dataSnapshot.child("email").getValue();
                String name = (String) dataSnapshot.child("name").getValue();
                mUserEmail.setText(useremail);
                mUsername.setText(username);
                mName.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                 Log.e(LOG_TAG,"Error whille updating profile");
            }
        });

    }

    private void initializeScreen() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mUsername=(TextView)findViewById(R.id.textview_username);
        mUserEmail=(TextView)findViewById(R.id.textview_useremail);
        mName=(TextView)findViewById(R.id.textview_name);
        mEditTextBio=(EditText)findViewById(R.id.edit_text_about_you);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayout=(LinearLayout)findViewById(R.id.linear_layout);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveEditProfile();
            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEditProfile() {
        if (!mEditTextBio.equals("")) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            final String user_id = mAuth.getCurrentUser().getUid();
            final DatabaseReference userReference = mDatabase.child(user_id);

            final String mUserBio = mEditTextBio.getText().toString();

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userReference.child("bio").setValue(mUserBio);
                    Snackbar snackbar = Snackbar.make(linearLayout, "Setting updated successfully", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

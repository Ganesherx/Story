package com.example.ganesh.story.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ganesh.story.R;
import com.example.ganesh.story.ui.authentication.LoginActivity;
import com.example.ganesh.story.ui.like.StoryLikeActivty;
import com.example.ganesh.story.ui.post.PostActivity;
import com.example.ganesh.story.ui.profile.ProfileActivity;
import com.example.ganesh.story.ui.search.SearchActivity;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getName();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeScrren();


        //DATABASE CONNECTION
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.e(LOG_TAG, "Database= " + mDatabase);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

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
}

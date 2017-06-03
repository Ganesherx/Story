package com.example.ganesh.story.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ganesh.story.R;
import com.example.ganesh.story.activeStory.BottomNavigationViewHelper;
import com.example.ganesh.story.activeStory.MainActivity;
import com.example.ganesh.story.ui.like.StoryLikeActivty;
import com.example.ganesh.story.ui.profile.ProfileActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                                break;
                            case R.id.action_like:
                                startActivity(new Intent(SearchActivity.this, StoryLikeActivty.class));
                                break;
                            case R.id.action_search:
                                break;
                            case R.id.action_profile:
                                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                                break;
                        }
                        return false;
                    }
                });

    }
}

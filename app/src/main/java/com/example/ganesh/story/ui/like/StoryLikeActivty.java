package com.example.ganesh.story.ui.like;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ganesh.story.R;
import com.example.ganesh.story.activeStory.BottomNavigationViewHelper;
import com.example.ganesh.story.activeStory.MainActivity;
import com.example.ganesh.story.ui.profile.ProfileActivity;
import com.example.ganesh.story.ui.search.SearchActivity;

public class StoryLikeActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_like_activty);

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
                                startActivity(new Intent(StoryLikeActivty.this, MainActivity.class));
                                break;
                            case R.id.action_like:
                                break;
                            case R.id.action_search:
                                startActivity(new Intent(StoryLikeActivty.this, SearchActivity.class));
                                break;
                            case R.id.action_profile:
                                startActivity(new Intent(StoryLikeActivty.this, ProfileActivity.class));
                                break;
                        }
                        return false;
                    }
                });
    }
}

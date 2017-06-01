package com.clabs.majorproject.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.clabs.majorproject.R;
import com.clabs.majorproject.util.Preference;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Profile");
        }
        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        nameTextView.setText(Preference.getUserName(UserProfileActivity.this));
        emailTextView.setText(Preference.getUserId(UserProfileActivity.this));
        try {
            Picasso.with(UserProfileActivity.this).load(Preference.getImageUri(UserProfileActivity.this)).into(imageView);
        } catch (Exception e) {
        }

    }
}

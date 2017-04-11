package com.clabs.majorproject.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clabs.majorproject.R;
import com.clabs.majorproject.models.ReviewModel;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.clabs.majorproject.util.Preference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StoreProfileActivity extends AppCompatActivity {

    ImageView storeImageView;
    TextView storeDescTextView;
    TextView storeAddressTextView;
    TextView followersTextView;
    TextView ratingTextView;
    EditText reviewEditText;
    Button submitButton;
    LinearLayout reviewLayout;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();

        context = StoreProfileActivity.this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(storeModel.getName());
        }

        storeImageView = (ImageView) findViewById(R.id.store_image_image_view);
        storeDescTextView = (TextView) findViewById(R.id.store_desc_text_view);
        storeAddressTextView = (TextView) findViewById(R.id.store_address_text_view);
        followersTextView = (TextView) findViewById(R.id.followers_text_view);
        ratingTextView = (TextView) findViewById(R.id.rating_text_view);
        reviewEditText = (EditText) findViewById(R.id.review_edit_text);
        submitButton = (Button) findViewById(R.id.submit_button);
        reviewLayout = (LinearLayout) findViewById(R.id.review_layout);
        Picasso.with(getApplicationContext()).load(storeModel.getImageUri()).into(storeImageView);

        submitButton.setOnClickListener(listener);


        storeDescTextView.setText(storeModel.getDescription());
        storeAddressTextView.setText(storeModel.getAddress());
        if (storeModel.getReviewModelList() != null)
            inflateReview(storeModel.getReviewModelList());
    }

    private void inflateReview(List<ReviewModel> reviewModelList) {
        reviewLayout.removeAllViews();
        for (ReviewModel reviewModel : reviewModelList) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = vi.inflate(R.layout.view_review, reviewLayout, false);
            TextView userNameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
            TextView userReviewTextView = (TextView) view.findViewById(R.id.user_review_text_view);
            userNameTextView.setText(reviewModel.getUserName());
            userReviewTextView.setText(reviewModel.getReview());

            reviewLayout.addView(view);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();
            String review = reviewEditText.getText().toString();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(storeModel.getCity())
                    .child("Store")
                    .child(storeModel.getStoreType())
                    .child(storeModel.getId());

            ReviewModel reviewModel = new ReviewModel();
            reviewModel.setReview(review);
            reviewModel.setUserId(Preference.getUserId(context));
            reviewModel.setUserName(Preference.getUserName(context));
            List<ReviewModel> reviewModelList;
            if (storeModel.getReviewModelList() == null) {
                reviewModelList = new ArrayList<>();
            } else {
                reviewModelList = storeModel.getReviewModelList();
            }
            reviewModelList.add(reviewModel);
            storeModel.setReviewModelList(reviewModelList);
            databaseReference.setValue(storeModel);

            ShopRegistrationSingleton.getInstance().setShopModel(storeModel);
            inflateReview(storeModel.getReviewModelList());
        }
    };
}

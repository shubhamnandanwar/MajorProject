package com.clabs.majorproject.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
    TextView verifyTextView;
    RatingBar averageRatingBar;
    LinearLayout reviewLayout;
    EditText reviewEditText;
    Context context;
    RatingBar ratingBar;
    Button rateButton;
    Button verifyButton;
    LinearLayout ratingLayout;
    TextView reportTextView;
    StoreModel storeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        storeModel = ShopRegistrationSingleton.getInstance().getShopModel();

        context = StoreProfileActivity.this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(storeModel.getName());
        }

        rateButton = (Button)findViewById(R.id.button_rate);
        verifyButton = (Button)findViewById(R.id.button_verify);
        verifyTextView = (TextView)findViewById(R.id.textView_verify);
        reportTextView = (TextView)findViewById(R.id.text_view_report);
        storeImageView = (ImageView) findViewById(R.id.store_image_image_view);
        storeDescTextView = (TextView) findViewById(R.id.store_desc_text_view);
        storeAddressTextView = (TextView) findViewById(R.id.store_address_text_view);
        averageRatingBar = (RatingBar) findViewById(R.id.rating_average);
        reviewLayout = (LinearLayout) findViewById(R.id.review_layout);
        ratingLayout = (LinearLayout)findViewById(R.id.rating_layout);
        Picasso.with(getApplicationContext()).load(storeModel.getImageUri()).into(storeImageView);

        reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
        verifyTextView.setText("Verified by "+storeModel.getVerifiedBy().size()+" users");
        storeDescTextView.setText(storeModel.getDescription());
        storeAddressTextView.setText(storeModel.getAddress());
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        if (storeModel.getReviewModelList() != null)
            inflateReview(storeModel.getReviewModelList());
        else
        ratingLayout.setVisibility(View.GONE);

        if(storeModel.getVerifiedBy()!=null){
            if(storeModel.getVerifiedBy().contains(Preference.getUserId(StoreProfileActivity.this))){
                verifyButton.setClickable(false);
                verifyButton.setText("Verified");
            }
        }
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeModel.getVerifiedBy().add(Preference.getUserId(getApplicationContext()));
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child(storeModel.getCity())
                        .child("Store")
                        .child(storeModel.getStoreType())
                        .child(storeModel.getId());
                databaseReference.setValue(storeModel);

                verifyButton.setClickable(false);
                verifyButton.setText("Verified");
                verifyTextView.setText("Verified by "+storeModel.getVerifiedBy().size()+" users");

            }
        });
    }
    private void report() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"shubhamnandanwar9776.com"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Report Shop - " + storeModel.getName());
        Email.putExtra(Intent.EXTRA_TEXT, "Hey Q Fish Team, " + "\n\nI want to report shop id - "+storeModel.getId()+" because");
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }
    private void inflateReview(List<ReviewModel> reviewModelList) {
        reviewLayout.removeAllViews();
        int totalRating = 0;
        if(reviewModelList.size()==0){
            ratingLayout.setVisibility(View.GONE);
        }
        else {
            ratingLayout.setVisibility(View.VISIBLE);
        }
        for (ReviewModel reviewModel : reviewModelList) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = vi.inflate(R.layout.view_review, reviewLayout, false);
            TextView userNameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
            TextView userReviewTextView = (TextView) view.findViewById(R.id.user_review_text_view);
            RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
            ratingBar.setRating((float) reviewModel.getRating());
            userNameTextView.setText(reviewModel.getUserName());
            userReviewTextView.setText(reviewModel.getReview());
            reviewLayout.addView(view);
            totalRating += reviewModel.getRating();
        }
        float averageRating = (float)totalRating/reviewModelList.size();
        averageRatingBar.setRating(averageRating);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private void showDialog() {
        final Dialog dialog = new Dialog(StoreProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.view_rate_review);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ratingBar=(RatingBar)dialog.findViewById(R.id.ratingBar);

        reviewEditText = (EditText) dialog.findViewById(R.id.review_edit_text);
        Button submitButton = (Button) dialog.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();
                String review = reviewEditText.getText().toString();
                int rating = (int)ratingBar.getRating();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child(storeModel.getCity())
                        .child("Store")
                        .child(storeModel.getStoreType())
                        .child(storeModel.getId());

                ReviewModel reviewModel = new ReviewModel();
                reviewModel.setReview(review);
                reviewModel.setRating(rating);
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
                dialog.hide();
            }
        });
        dialog.show();
    }
}

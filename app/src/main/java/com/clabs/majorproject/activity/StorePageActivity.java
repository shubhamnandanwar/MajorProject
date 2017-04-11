package com.clabs.majorproject.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clabs.majorproject.R;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.squareup.picasso.Picasso;

public class StorePageActivity extends AppCompatActivity {

    ImageView storeImageView;
    TextView storeNameTextView;
    TextView storeDescTextView;
    TextView storeAddressTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storeImageView = (ImageView)findViewById(R.id.store_image_image_view);
        storeNameTextView = (TextView)findViewById(R.id.store_name_text_view);
        storeDescTextView = (TextView)findViewById(R.id.store_desc_text_view);
        storeAddressTextView = (TextView)findViewById(R.id.store_address_text_view);

        StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();
        Picasso.with(getApplicationContext()).load(storeModel.getImageUri()).into(storeImageView);

        storeNameTextView.setText(storeModel.getName());
        storeDescTextView.setText(storeModel.getDescription());
        storeAddressTextView.setText(storeModel.getAddress());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

package com.clabs.majorproject.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.clabs.majorproject.R;
import com.clabs.majorproject.fragments.StoreDetailsFragment;
import com.clabs.majorproject.fragments.StoreImagePickerFragment;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.models.LatLng;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.clabs.majorproject.util.CommonUtilities;
import com.clabs.majorproject.util.Constants;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StoreRegistrationActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    protected StoreDetailsFragment storeDetailsFragment = new StoreDetailsFragment();
    protected StoreImagePickerFragment storeImagePickerFragment = new StoreImagePickerFragment();
    //TODO - select image in fixed dimension
    //TODO - Submit an image capture by camera for shop verification.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_registration);

        setToolbar();
        setAdapter();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerShopOnDatabase();
            }
        });
        progressDialog = CommonUtilities.startProgressDialog(StoreRegistrationActivity.this, "Registering...");

    }



    private void setAdapter(){
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getApplicationContext(), R.color.icons));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.icons));
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.action_register_shop));
        }
    }

    //TODO - field validation.
    private void registerShopOnDatabase() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog.show();
        storeDetailsFragment.setStoreDetails();
        storeImagePickerFragment.setStoreDetails();

        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault());
        Date date = new Date();
        String str = dateFormat.format(date);
        String fileName = "Coder_" + str + ".jpg";
        StorageReference filePath = storageReference.child("photos").child(fileName);
        filePath.putFile(Uri.parse(ShopRegistrationSingleton.getInstance().getShopModel().getImageUri())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                Snackbar.make(fab, "Successfully Posted", Snackbar.LENGTH_LONG).show();
                if (downloadUri != null) {
                    StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();

                    String id = CommonUtilities.getRandomString();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(storeModel.getCity())
                            .child("Store")
                            .child(storeModel.getStoreType())
                            .child(id);
                    storeModel.setImageUri(downloadUri.toString());
                    LatLng latLng = new LatLng();
                    latLng.setLatitude(25.75);
                    latLng.setLongitude(75.75);
                    storeModel.setLatLng(latLng);
                    storeModel.setId(id);
                    ShopRegistrationSingleton.getInstance().setShopModel(storeModel);
                    databaseReference.setValue(ShopRegistrationSingleton.getInstance().getShopModel());
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(fab, "Registration Failed", Snackbar.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return storeImagePickerFragment;
                case 1:
                    return storeDetailsFragment;
            }
            return new StoreImagePickerFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}

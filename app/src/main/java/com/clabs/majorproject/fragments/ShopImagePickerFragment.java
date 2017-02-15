package com.clabs.majorproject.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.clabs.majorproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

/**
 * Created by shubh on 15-02-2017.
 */

public class ShopImagePickerFragment extends Fragment {

    public static final int GALLERY_REQUEST = 1;
    private ImageView addImageView;
    private EditText shopNameEditText;
    private EditText shopDescriptionEditText;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_image_picker, container, false);
        addImageView = (ImageView) view.findViewById(R.id.pick_image_image_view);
        shopDescriptionEditText = (EditText) view.findViewById(R.id.description_text_view);
        shopNameEditText = (EditText) view.findViewById(R.id.shop_name_text_view);

        //storageReference = FirebaseStorage.getInstance().getReference();
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            addImageView.setImageURI(imageUri);
        }
    }
}

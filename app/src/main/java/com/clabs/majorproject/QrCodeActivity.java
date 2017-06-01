package com.clabs.majorproject;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.clabs.majorproject.models.OfferModel;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.util.CommonUtilities;
import com.clabs.majorproject.util.Constants;
import com.clabs.majorproject.util.Preference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.Manifest;

public class QrCodeActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    Button saveQrButton;
    EditText productEditText;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    StoreModel storeModel;
    String storeId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Offers");
        }
        storeId = Preference.getStoreId(QrCodeActivity.this);
        getRestaurantLocationFromDatabase();
        imageView = (ImageView) findViewById(R.id.imageView);
        productEditText = (EditText) findViewById(R.id.edit_text_product_desc);
        button = (Button) findViewById(R.id.button);
        saveQrButton = (Button) findViewById(R.id.save_qr_button);
        saveQrButton.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfferModel offerModel = new OfferModel();
                offerModel.setDiscount(0);
                offerModel.setOfferId(CommonUtilities.getRandomString());
                offerModel.setProductDesc(productEditText.getText().toString());
                offerModel.setStoreId(Preference.getStoreId(QrCodeActivity.this));
                storeModel.setOfferModel(offerModel);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child(storeModel.getCity())
                        .child("Store")
                        .child(storeModel.getStoreType())
                        .child(storeId);

                databaseReference.setValue(storeModel);
                Gson g = new Gson();
                try {
                    bitmap = TextToImageEncode(g.toJson(offerModel));
                    imageView.setImageBitmap(bitmap);
                    button.setVisibility(View.GONE);
                    saveQrButton.setVisibility(View.VISIBLE);
                    saveQrButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SaveImage(bitmap);
                            Snackbar.make(saveQrButton,"QR Code Saved",Snackbar.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SaveImage(Bitmap finalBitmap) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "Q_Fish");

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault());
        String str = dateFormat.format(date);
        String fname = "Offer" + "-" + str + ".jpg";
        if ((!dir.exists() && !dir.mkdir())) {
            mkFolder("Q_Fish");
            Log.d("Sharing Photo", "Can not create Directory");
        }

        File file = new File(dir, fname);

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Snackbar.make(imageView,"QR Code Image saved successfully.",Snackbar.LENGTH_LONG);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(imageView,"Something went wrong.",Snackbar.LENGTH_LONG);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public int mkFolder(String folderName){
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)){
            Log.d("myAppName", "Error: external storage is unavailable");
            return 0;
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("myAppName", "Error: external storage is read only.");
            return 0;
        }
        Log.d("myAppName", "External storage is not read only or unavailable");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("myAppName", "permission:WRITE_EXTERNAL_STORAGE: NOT granted!");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(QrCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),folderName);
        int result = 0;
        if (folder.exists()) {
            Log.d("myAppName","folder exist:"+folder.toString());
            result = 2; // folder exist
        }else{
            try {
                if (folder.mkdir()) {
                    Log.d("myAppName", "folder created:" + folder.toString());
                    result = 1; // folder created
                } else {
                    Log.d("myAppName", "creat folder fails:" + folder.toString());
                    result = 0; // creat folder fails
                }
            }catch (Exception ecp){
                ecp.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }
        }
    }
    private void getRestaurantLocationFromDatabase() {
        final ProgressDialog progressDialog = CommonUtilities.startProgressDialog(QrCodeActivity.this);
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Preference.getCity(getApplicationContext()))
                .child("Store")
                .child(Preference.getStoreType(QrCodeActivity.this));
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    StoreModel model = dataSnapshot.getValue(StoreModel.class);
                    if (model.getId().equals(storeId)) {
                        storeModel = model;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}

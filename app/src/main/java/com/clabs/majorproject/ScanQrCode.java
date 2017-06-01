package com.clabs.majorproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.util.CommonUtilities;
import com.clabs.majorproject.util.Constants;
import com.clabs.majorproject.util.Preference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanQrCode extends AppCompatActivity implements View.OnClickListener{
    private Button buttonScan;
    StoreModel storeModel;
    String storeId;
    //qr code scanner object
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        storeId = Preference.getStoreId(ScanQrCode.this);
        buttonScan = (Button) findViewById(R.id.buttonScan);

        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);
        getLocationFromDatabase();
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    String content = result.getContents();
                    if(storeModel.getOfferModel().getCustomerList().contains(result.getContents()))
                        alertInvalidUser();
                    else{
                        storeModel.getOfferModel().getCustomerList().add(result.getContents());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(storeModel.getCity())
                                .child("Store")
                                .child(storeModel.getStoreType())
                                .child(storeModel.getId());
                        databaseReference.setValue(storeModel);
                        alertValidUser();
                    }

                    //converting the data to json
                    //textViewName.setText(result.getContents());
                    /*JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));*/
                } catch (Exception e) {
                    e.printStackTrace();
                    alertInvalidUser();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void alertInvalidUser(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ScanQrCode.this);
        String userName = Preference.getUserName(ScanQrCode.this).split(" ")[0];
        alert.setTitle("Hello, " + userName)
                .setMessage("User had already availed this offer.")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void alertValidUser(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ScanQrCode.this);
        String userName = Preference.getUserName(ScanQrCode.this).split(" ")[0];
        alert.setTitle("Hello, " + userName)
                .setMessage("User can have this offer.")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }

    private void getLocationFromDatabase() {
        final ProgressDialog progressDialog = CommonUtilities.startProgressDialog(ScanQrCode.this);
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Preference.getCity(getApplicationContext()))
                .child("Store")
                .child(Constants.RESTAURANT);
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
}

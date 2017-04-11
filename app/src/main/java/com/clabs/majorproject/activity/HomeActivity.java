package com.clabs.majorproject.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.clabs.majorproject.GeofenceTransitionsIntentService;
import com.clabs.majorproject.R;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.clabs.majorproject.util.CommonUtilities;
import com.clabs.majorproject.util.Constants;
import com.clabs.majorproject.util.Preference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    public static final int LOCATION_UPDATE_INTERVAL = 15000; //IN MILLI SECOND
    private GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DrawerLayout drawerLayout;
    double currentLatitude = 8.5565795, currentLongitude = 76.8810227;
    private List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    public static final String TAG = "Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!isNetworkAvailable()) {
            showNoInternetConnectionDialog();
        }
        if (!isGooglePlayServicesAvailable()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Google Play Service Not Available")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        }
        mGeofenceList = new ArrayList<>();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpMap(StoreModel storeModel) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(storeModel.getLatLng().getLatitude(), storeModel.getLatLng().getLongitude());

        TextView text = new TextView(getApplicationContext());
        text.setText(storeModel.getName());
        text.setPaddingRelative(8, 8, 8, 8);
        IconGenerator generator = new IconGenerator(getApplicationContext());
        generator.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholder));
        generator.setContentView(text);
        Bitmap icon = generator.makeIcon();

        markerOptions.position(latLng);
        markerOptions.title(storeModel.getName())
                .icon(BitmapDescriptorFactory.fromBitmap(icon));
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(storeModel);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void getRestaurantLocationFromDatabase() {
        final ProgressDialog progressDialog = CommonUtilities.startProgressDialog(HomeActivity.this);
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
                    setUpMap(model);
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

    private void showNoInternetConnectionDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        alert.setTitle("No Internet Connection")
                .setMessage("We can not detect any internet connectivity. Please check your internet connection and try again.")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            finish();
                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                StoreModel storeModel = (StoreModel) marker.getTag();
                ShopRegistrationSingleton.getInstance().setShopModel(storeModel);
                Intent intent = new Intent(HomeActivity.this, StoreProfileActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void createGeofences(double latitude, double longitude) {
        String id = UUID.randomUUID().toString();
        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, 20000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        mGeofenceList.add(fence);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_register_shop) {
            Intent intent = new Intent(HomeActivity.this, StoreRegistrationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_restaurant) {
            getRestaurantLocationFromDatabase();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        createGeofences(currentLatitude, currentLongitude);

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Saving Geofence");

                    } else {
                        Log.e(TAG, "Registering geofence failed: " + status.getStatusMessage() +
                                " : " + status.getStatusCode());
                    }
                }
            });

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, "Error");
        }
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onConnectionSuspended(int i) {
        CommonUtilities.showSnackbar(drawerLayout, "Connection Suspended", getApplicationContext());

    }

    boolean isFirstTime = true;

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (isFirstTime) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            isFirstTime = false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CommonUtilities.showSnackbar(drawerLayout, "Connection Failed", getApplicationContext());

    }
}

package com.smartcitypune.smartpune;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;

    private String lat;
    private String lng;

    private FirebaseAuth mAuth;
    private String TAG = "DrawerActivity";

    private String name;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView userNameTextView = (TextView) headerView.findViewById(R.id.userNameTextView);
        TextView userContactTextView = (TextView) headerView.findViewById(R.id.userContactTextView);
        ImageView userPhotoImageView = (ImageView) headerView.findViewById(R.id.userPhotoImageView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        name = currentUser.getDisplayName();
        if (currentUser != null) {
            userNameTextView.setText(currentUser.getDisplayName());
            userContactTextView.setText(currentUser.getEmail());
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                new DownloadImageTask(userPhotoImageView).execute(photoUrl.toString());
            }
        } else {
            Log.d(TAG, "onCreate: " + "currentUser is null");
        }

        CardView callAmbulance = (CardView) findViewById(R.id.emergency_services_ambulance);
        callAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "onClick:");
                requestAmbulanceService();


            }
        });


        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    private void requestAmbulanceService() {
        //raise the issue with the backend
        dialog = ProgressDialog.show(DrawerActivity.this, "ALERTING",
                "Contacting the nearest available authorities.", true);
        dialog.show();

        String url = "";
        try {
            url = "http://192.168.43.122:8000/distanceBasedServices/?lat=" + URLEncoder.encode(lat, "UTF-8").toString()
                    + "&lng=" + URLEncoder.encode(lng, "UTF-8").toString() + "&name=" +
                    URLEncoder.encode(name, "UTF-8").toString();
            Log.i(TAG, "requestAmbulanceService: " + url);

        } catch (UnsupportedEncodingException e) {
            url = "";
            e.printStackTrace();
        }

        GetServiceResponse getServiceResponse = new GetServiceResponse();
        String ambulanceID = null;
        try {
            ambulanceID = getServiceResponse.execute(url).get();
            if (!ambulanceID.equals("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
                builder.setTitle("ALERTED");

                builder.setMessage("Ambulance " + ambulanceID + " has been alerted and is on its way to your location.\nPhone No.: **********");
                final String finalAmbulanceID = ambulanceID;
                builder.setPositiveButton("TRACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(DrawerActivity.this, ServiceLocationMapsActivity.class);
                        intent.putExtra("dbPath", "data/services/ambulances/" + finalAmbulanceID);
                        startActivity(intent);
                    }
                });
                dialog.dismiss();
                builder.create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
                builder.setTitle("ALERT!");
                builder.setMessage("We'll be assigning you an ambulance in some time. Please try again.");
                builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestAmbulanceService();
                        Log.i(TAG, "onClick: calling again requestAmbulanceService");
                    }
                });
                dialog.dismiss();
                builder.create().show();
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "requestAmbulanceService: thread started");


    }


    class GetServiceResponse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            URL website = null;
            try {
                website = new URL(strings[0]);

                URLConnection connection = website.openConnection();
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNext()) {
                    builder.append(scanner.nextLine());
                }
                scanner.close();
                String key = builder.toString();
                Log.i(TAG, "doInBackground: " + key);
                return key;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_emergency_contacts) {
            // edit saved emergency contacts
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_road_safety) {
            startActivity(new Intent(DrawerActivity.this, RoadSafetyDashboardActivity.class));
        } else if (id == R.id.nav_community) {

        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(DrawerActivity.this, ViewNotificationsActivity.class));
        } else if (id == R.id.nav_polls) {
            startActivity(new Intent(DrawerActivity.this, DisplayPolls.class));
        } else if (id == R.id.nav_viral_messages) {
            startActivity(new Intent(DrawerActivity.this, ViralMessageDashboardActivity.class));
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_submissions) {

        } else if (id == R.id.nav_preferences) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        mCurrentLocation = location;
        lat = String.valueOf(mCurrentLocation.getLatitude());
        lng = String.valueOf(mCurrentLocation.getLongitude());
        Log.i(TAG, "Location: " + "At Time: " + DateFormat.getTimeInstance().format(new Date()) + "\n" +
                "Latitude: " + lat + "\n" +
                "Longitude: " + lng + "\n" +
                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                "Provider: " + mCurrentLocation.getProvider());

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed");
        }
    }


}

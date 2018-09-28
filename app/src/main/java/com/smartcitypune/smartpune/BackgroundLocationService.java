package com.smartcitypune.smartpune;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class BackgroundLocationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        FusedLocationActivity fusedLocationActivity = new FusedLocationActivity();
        fusedLocationActivity.createLocationRequest();

        fusedLocationActivity.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(fusedLocationActivity)
                .addOnConnectionFailedListener(fusedLocationActivity)
                .build();


    }

    class FusedLocationActivity implements
            LocationListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "LocationActivity";
        private static final long INTERVAL = 1000 * 10;
        private static final long FASTEST_INTERVAL = 1000 * 5;

        LocationRequest mLocationRequest;
        GoogleApiClient mGoogleApiClient;
        Location mCurrentLocation;

        private String lat;
        private String lng;



        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }


        public void onStart() {
            Log.d(TAG, "onStart fired");
            mGoogleApiClient.connect();
        }

        public void onStop() {
            Log.d(TAG, "onStop fired");
            mGoogleApiClient.disconnect();
            Log.d(TAG, "isConnected: " + mGoogleApiClient.isConnected());
        }


        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected - isConnected: " + mGoogleApiClient.isConnected());
            startLocationUpdates();
        }

        protected void startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        protected void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped");
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        int id = android.os.Process.myPid();
        android.os.Process.killProcess(id);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}


package com.smartcitypune.smartpune;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ServiceLocationMaps";
    private GoogleMap mMap;
    private String dbPath;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_location_maps);

        Intent incomingIntent = getIntent();
        dbPath = incomingIntent.getStringExtra("dbPath");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final MarkerOptions marker = new MarkerOptions().position(new LatLng(19.859678, 75.336298))
                .title("Aid");
        mMap.addMarker(marker);

        mDatabase = FirebaseDatabase.getInstance().getReference(dbPath);
        ValueEventListener viralMessagesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ServiceCase serviceCase = dataSnapshot.getValue(ServiceCase.class);

                Log.i(TAG, "onDataChange: "+serviceCase.toString());
                LatLng latLng = new LatLng(serviceCase.getLat(), serviceCase.getLng());
                marker.position(latLng);
                Log.i(TAG, "onDataChange: " + latLng.toString());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ServiceLocationMaps", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(viralMessagesValueEventListener);


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


}

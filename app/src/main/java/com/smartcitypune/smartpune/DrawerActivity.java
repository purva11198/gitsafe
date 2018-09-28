package com.smartcitypune.smartpune;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private String TAG = "DrawerActivity";

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

        final ProgressDialog[] dialog = new ProgressDialog[1];
        CardView callMonkeyCatchers = (CardView) findViewById(R.id.emergency_services_ambulance);
        callMonkeyCatchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //raise the issue with the backend
                dialog[0] = ProgressDialog.show(DrawerActivity.this, "ALERTING",
                        "Contacting the nearest available authorities.", true);
                dialog[0].show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog[0].dismiss();
                        //retrieve contact details and show them to the user
                        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
                        builder.setTitle("ALERTED");
                        builder.setMessage("Ambulance AB-154 has been alerted and is on its way to your location.\nPhone No.: 9405458877");
                        builder.setPositiveButton("TRACK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(DrawerActivity.this, ServiceLocationMapsActivity.class);
                                intent.putExtra("dbPath", "data/services/ambulances/-LNRZjmHWcvFoBav39HE");
                                startActivity(intent);
                            }
                        });
                        builder.create().show();
                    }
                }, 1000);

            }
        });


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

}

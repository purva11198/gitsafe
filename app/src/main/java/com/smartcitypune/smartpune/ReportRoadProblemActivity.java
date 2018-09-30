package com.smartcitypune.smartpune;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

public class ReportRoadProblemActivity extends AppCompatActivity
        implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;


    private static final String TAG = "ReportRoadProblem";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final int READ_REQUEST_CODE = 42;
    private FirebaseStorage storage;
    private String fireBaseStorageReferencePath;
    private Spinner categorySpinner;
    private EditText messageEditText;
    private Button submitButton;
    private ImageButton attachFileImageButton;
    private String selectedPath;
    private String userId;
    private TextView file_selection_status;
    private double lat = 0;
    private double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_road_problem);
        setTitle("Submit a viral message");

        mAuth = FirebaseAuth.getInstance();
        categorySpinner = (Spinner) findViewById(R.id.message_category);
        messageEditText = (EditText) findViewById(R.id.viral_message_text);
        submitButton = (Button) findViewById(R.id.submit_message);
        attachFileImageButton = (ImageButton) findViewById(R.id.attach_file_image_button);
        file_selection_status = (TextView) findViewById(R.id.file_selection_status);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        mDatabase = FirebaseDatabase.getInstance().getReference("data/road-problems");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        Spinner dropdown = findViewById(R.id.message_category);
        String[] items = new String[]{"Pothole", "Poles", "Trees", "Spilt garbage", "Cracks", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        storage = FirebaseStorage.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(selectedPath);
            }
        });

        attachFileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            file_selection_status.setVisibility(View.VISIBLE);
            Uri selectedImageUri = resultData.getData();
            Log.d(TAG, "onActivityResult: " + selectedImageUri.toString());
            selectedPath = getRealPathFromURI(selectedImageUri);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        Log.i(TAG, "getRealPathFromURI: " + uri.toString());
        Log.i(TAG, "getRealPathFromURI: " + wholeID);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        Cursor cursor = null;
        int columnIndex = 0;
        String[] column = null;

        column = new String[]{MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);
        columnIndex = cursor.getColumnIndex(column[0]);


        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }


    private void uploadFile(String file) {

        if (file == null) {
            addInFireBaseDatabase();
            return;
        }

        StorageReference storageRef = storage.getReference();
        Log.d(TAG, "uploadFile: " + file.toString());
        Log.d(TAG, "uploadFile: " + file.substring(file.lastIndexOf("/")));
        StorageReference viralMessageStorageReference = storageRef.child("road-problems/" + file.substring(file.lastIndexOf("/")));


        InputStream stream = null;
        try {
            final int ts = (int) ((new File(file)).length());
            stream = new FileInputStream(new File(file));

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading file.");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            UploadTask uploadTask = viralMessageStorageReference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(ReportRoadProblemActivity.this, "Failed to upload the file.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    StorageMetadata completedTaskSnapshot = taskSnapshot.getMetadata();
                    fireBaseStorageReferencePath = completedTaskSnapshot.getReference().toString();
                    addInFireBaseDatabase();
                    Toast.makeText(ReportRoadProblemActivity.this, "File uploaded", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / ts);
                    Log.i(TAG, "onProgress: \n"
                            + taskSnapshot.getBytesTransferred() + "\n" +
                            (ts * 1024) +
//                            "\n" + taskSnapshot.getTotalByteCount()+
                            "\n" + progress);

                    progressDialog.setProgress((int) progress);
//                    Log.i(TAG, "onProgress: "+progressDialog.getProgress());
//                    progressDialog.setMessage("Progress " + (int) progress + "%");
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addInFireBaseDatabase() {
        String category = categorySpinner.getSelectedItem().toString();
        String message = messageEditText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Integer date = Integer.parseInt(sdf.format(new Date()));


        if (!message.isEmpty() || fireBaseStorageReferencePath != null) {
            RoadProblem roadProblem;
            if (fireBaseStorageReferencePath != null) {
                roadProblem = new RoadProblem(userId, lat, lng, message, category, date, fireBaseStorageReferencePath, "pending");
            } else {
                roadProblem = new RoadProblem(userId, lat, lng, message, category, date, "no-file-provided", "pending");
            }
            mDatabase.push().setValue(roadProblem);
            Toast.makeText(this, "Problem reported.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Please provide a file or a message.", Toast.LENGTH_SHORT).show();
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
        lat = Double.valueOf(mCurrentLocation.getLatitude());
        lng = Double.valueOf(mCurrentLocation.getLongitude());
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

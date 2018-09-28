package com.smartcitypune.smartpune;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportViralMessage extends AppCompatActivity {

    private static final String TAG = "ReportViralMessage";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_viral_message);
        setTitle("Submit a viral message");

        mAuth = FirebaseAuth.getInstance();
        categorySpinner = (Spinner) findViewById(R.id.message_category);
        messageEditText = (EditText) findViewById(R.id.viral_message_text);
        submitButton = (Button) findViewById(R.id.submit_message);
        attachFileImageButton = (ImageButton) findViewById(R.id.attach_file_image_button);
        file_selection_status = (TextView) findViewById(R.id.file_selection_status);

        mDatabase = FirebaseDatabase.getInstance().getReference("data/viral-messages");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        Spinner dropdown = findViewById(R.id.message_category);
        String[] items = new String[]{"General", "Health", "News", "Banking", "Other"};
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
                intent.setType("*/*");
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

        if (wholeID.contains("image")) {
            column = new String[]{MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
            columnIndex = cursor.getColumnIndex(column[0]);
        } else if (wholeID.contains("video")) {
            column = new String[]{MediaStore.Video.Media.DATA};
            String sel = MediaStore.Video.Media._ID + "=?";
            cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
            columnIndex = cursor.getColumnIndex(column[0]);

        } else if (wholeID.contains("audio")) {
            column = new String[]{MediaStore.Audio.Media.DATA};
            String sel = MediaStore.Audio.Media._ID + "=?";
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
            columnIndex = cursor.getColumnIndex(column[0]);
        }


        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public String getRealPathFromURIOld(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
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
        StorageReference viralMessageStorageReference = storageRef.child("viral-messages/" + file.substring(file.lastIndexOf("/")));


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
                    Toast.makeText(ReportViralMessage.this, "Failed to upload the file.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    StorageMetadata completedTaskSnapshot = taskSnapshot.getMetadata();
                    fireBaseStorageReferencePath = completedTaskSnapshot.getReference().toString();
                    addInFireBaseDatabase();
                    Toast.makeText(ReportViralMessage.this, "File uploaded", Toast.LENGTH_SHORT).show();

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
            ViralMessage viralMessage;
            if (fireBaseStorageReferencePath != null) {
                viralMessage = new ViralMessage(userId, message, category, date, fireBaseStorageReferencePath, "un-assessed");
            } else {
                viralMessage = new ViralMessage(userId, message, category, date, "no-file-provided", "un-assessed");
            }
            mDatabase.push().setValue(viralMessage);
            Toast.makeText(this, "Message reported.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Please provide a file or a message.", Toast.LENGTH_SHORT).show();
        }

    }

}

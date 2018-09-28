package com.smartcitypune.smartpune;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewViralMessageActivity extends AppCompatActivity {

    private String TAG = "ViewViralMessage";
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.viral_message_header));
        setContentView(R.layout.activity_view_viral_message_dynamic);

        Intent incomingIntent = getIntent();
        final ViralMessage viralMessage = (ViralMessage) incomingIntent.getSerializableExtra("viralMessage");

        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView categoryTextView = findViewById(R.id.categoryTextView);
        TextView statusTextView = findViewById(R.id.statusTextView);
        TextView messageTextView = findViewById(R.id.messageDescriptionTextView);
        final ImageView viralMessageImageView = findViewById(R.id.viralMessageImageView);
        final ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);
        final FrameLayout viralMessageFrameLayout = findViewById(R.id.viralMessageFrameLayout);


        dateTextView.setText(String.valueOf(dateTextView.getText()) + Utilities.getDate(viralMessage.getDate()));
        categoryTextView.setText(String.valueOf(categoryTextView.getText()) + viralMessage.getCategory().toString().toUpperCase());
        statusTextView.setText(String.valueOf(statusTextView.getText()) + viralMessage.getStatus().toString().toUpperCase());

        if (!viralMessage.getMessage_text().isEmpty()) {
            messageTextView.setText(viralMessage.getMessage_text());
        } else {
            messageTextView.setText("No description provided.");
        }

        String firebaseStorageReferencePath = viralMessage.getFireStorageReference();
        if (!firebaseStorageReferencePath.equals("no-file-provided")) {
            storage = FirebaseStorage.getInstance();
            StorageReference gsReference = storage.getReferenceFromUrl(viralMessage.getFireStorageReference());

            final long ONE_MEGABYTE = 1024 * 1024;
            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    loadingProgressBar.setVisibility(View.GONE);
                    viralMessageImageView.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viralMessageImageView.setImageBitmap(bitmap);
                    if (viralMessage.getStatus().equalsIgnoreCase("fake")) {
                        ((ImageView) findViewById(R.id.fake_overlay)).setVisibility(View.VISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } else {
            viralMessageFrameLayout.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.attachedFileTextView)).setText("No file provided.");
        }
    }
}

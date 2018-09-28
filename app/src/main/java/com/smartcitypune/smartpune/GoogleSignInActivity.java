package com.smartcitypune.smartpune;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

public class GoogleSignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign In was successful", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);
//                startActivity(new Intent(GoogleSignInActivity.this, DrawerActivity.class));
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
//            startActivity(new Intent(GoogleSignInActivity.this, FusedLocationActivity.class));
            startActivity(new Intent(GoogleSignInActivity.this, DrawerActivity.class));

//            Intent intent = new Intent(GoogleSignInActivity.this, ServiceLocationMapsActivity.class);
//            intent.putExtra("dbPath","data/services/ambulances/-LNRZjmHWcvFoBav39HE");
//            startActivity(intent);


//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?saddr=19.873154, 75.328350&daddr=19.869506, 75.336332"));
//            startActivity(intent);


//            signOut();
//            revokeAccess();
//            displayMessage("Already signed in.");
        }
    }

    private void updateUI(FirebaseUser user) {
        String dummyText = null;
        if (user != null) {
            dummyText = user.getDisplayName()
                    + "\nEmail: " + user.getEmail()
                    + "\nName: " + user.getDisplayName()
                    + "\nPhoneNumber: " + user.getPhoneNumber()
                    + "\nProviderId: " + user.getProviderId()
                    + "\nUid: " + user.getUid()
                    + "\nMetaData: CR = " + user.getMetadata().getCreationTimestamp() + " LSI = " + user.getMetadata().getLastSignInTimestamp()
                    + "\nProviderId: " + user.getProviderId()
                    + "\n";

        } else {
            dummyText = "null object";
        }
//        dummyTextView.setText(dummyText);
        Log.i(TAG, "updateUI: " + dummyText);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseMessaging.getInstance().subscribeToTopic("govt")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String msg = "Subscribed to in-app notifications.";
                                            if (!task.isSuccessful()) {
                                                msg = "Could not subscribe to in-app notifications.";
                                            }
                                            Log.d(TAG, msg);
                                            Toast.makeText(GoogleSignInActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });

// check if a new account is created or whether the profile information is incomplete
                            AlertDialog.Builder builder = new AlertDialog.Builder(GoogleSignInActivity.this);
                            builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    startActivity(new Intent(EditProfileActivity.this, DrawerActivity.class));
                                }
                            });
                            builder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(GoogleSignInActivity.this, DrawerActivity.class));
                                }
                            });
                            builder.setTitle("Profile incomplete")
                                    .setMessage("Basic information retrieved from your Google account. Fill in the remaining details.");
                            AlertDialog dialog = builder.create();
                            dialog.show();


                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            displayMessage("signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.google_sign_in_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    public void signInWithEmailAndPassword() {

        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                displayMessage("signInWithEmail:success");
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                displayMessage(task.getException().getMessage());
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(getApplicationContext(), "Authentication failed. : " + task.getException().getMessage(),
//                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        } else {
        }
    }

    public void displayMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNeutralButton("OKAY", null)
                .create().show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.google_sign_in_button) {
            signIn();
        } else if (i == R.id.email_sign_in_button) {
            signInWithEmailAndPassword();
//            signOut();
        }
//        else if (i == R.id.disconnect_button) {
//            revokeAccess();
//        }
    }
}

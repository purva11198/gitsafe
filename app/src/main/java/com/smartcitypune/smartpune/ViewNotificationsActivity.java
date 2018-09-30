

package com.smartcitypune.smartpune;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ViewNotificationsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ViewNotificationsAdapter mAdapter;
    private DatabaseReference mDatabase;
    public ArrayList<NotificationCase> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        myToolbar.setTitle("All Notifications");
        setSupportActionBar(myToolbar);

        final ProgressDialog dialog = ProgressDialog.show(ViewNotificationsActivity.this, "Loading",
                "Retrieving all sent notifications.", true);
        dialog.show();

        myDataset = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("/data/notifications");
        ValueEventListener viralMessagesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    NotificationCase notificationCase = userDataSnapshot.getValue(NotificationCase.class);
                    myDataset.add(notificationCase);
                }
                dialog.dismiss();
                Collections.reverse(myDataset);
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewNotifications", "loadPost:onCancelled", databaseError.toException());
                dialog.dismiss();
            }
        };
        mDatabase.addValueEventListener(viralMessagesValueEventListener);


    }

    private void setData() {
        mRecyclerView = (RecyclerView) findViewById(R.id.view_viral_messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ViewNotificationsAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }


}

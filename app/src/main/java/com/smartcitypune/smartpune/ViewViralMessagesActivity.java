package com.smartcitypune.smartpune;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Collections;

public class ViewViralMessagesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ViewViralMessagesAdapter mAdapter;
    private DatabaseReference mDatabase;
    public ArrayList<ViralMessage> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_viral_messages);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        myToolbar.setTitle(R.string.viral_message);
        setSupportActionBar(myToolbar);

        final ProgressDialog dialog = ProgressDialog.show(ViewViralMessagesActivity.this, "Loading",
                "Retrieving latest viral messages.", true);
        dialog.show();

        myDataset = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("/data/viral-messages");
        ValueEventListener viralMessagesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    ViralMessage viralMessage = userDataSnapshot.getValue(ViralMessage.class);
                    myDataset.add(viralMessage);
                }
                dialog.dismiss();
                Collections.reverse(myDataset);
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewViralMessages", "loadPost:onCancelled", databaseError.toException());
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
        mAdapter = new ViewViralMessagesAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_emergency_services) {
            Toast.makeText(ViewViralMessagesActivity.this, "Launch emergency screen.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void viewSelectedViralMessage(View view) {
        Intent intent = new Intent(ViewViralMessagesActivity.this, ViewViralMessageActivity.class);
        ViralMessage viralMessage = (ViralMessage) view.getTag();
        intent.putExtra("viralMessage", viralMessage);
        startActivity(intent);
    }
}

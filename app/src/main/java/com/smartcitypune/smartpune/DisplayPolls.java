package com.smartcitypune.smartpune;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayPolls extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ref;

    public List<Poll> input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_polls);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        myToolbar.setTitle("Recent Polls");
        setSupportActionBar(myToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        final ProgressDialog dialog = ProgressDialog.show(DisplayPolls.this, "Loading",
                "Retrieving latest polls.", true);
        dialog.show();

        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("data/polls");
        input = new ArrayList<>();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userdataSnapshot : dataSnapshot.getChildren()) {
                    Poll post = userdataSnapshot.getValue(Poll.class);
                    input.add(post);
                    System.out.println(input.toString());
                }
                dialog.dismiss();
                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //inside contents

        mAdapter = new CustomAdapterForPolls(input);
        recyclerView.setAdapter(mAdapter);
    }

    public void setData() {


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
}

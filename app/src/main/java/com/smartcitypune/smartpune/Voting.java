package com.smartcitypune.smartpune;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Voting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        final LinearLayout layout = findViewById(R.id.voting_layout);
        Intent intent = getIntent();
        final Poll poll;
        poll = (Poll) intent.getExtras().get("Voting");
        TextView question=layout.findViewById(R.id.question);
        question.setText(poll.getQuestion());
        TextView date=layout.findViewById(R.id.date);
        date.setText(poll.getStartdate()+" - "+poll.getEnddate());
        final ProgressBar pb = layout.findViewById(R.id.pb);
        final ProgressBar pb2 = layout.findViewById(R.id.pb2);
        final ArrayList<Button> btnlist = new ArrayList<>();
        // int position=0;
        for(final String response:poll.getResponses()){
            //  position++;

            final Button buttonView = new Button(this);

            // set some properties of rowTextView or something
            buttonView.setText(response);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(5, 50, 5, 50);
//            layout.setLayoutParams(params);

            // add the textview to the linearlayout
            layout.addView(buttonView);
            btnlist.add(buttonView);
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Button btn:btnlist){
                        layout.removeView(btn);
                    }
                    layout.removeView(pb);
                    layout.removeView(pb2);
                    layout.addView(pb);
                    layout.addView(pb2);

                    pb.setVisibility(View.VISIBLE);
                    pb2.setVisibility(View.VISIBLE);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pb.setProgress(25,true);
                        pb2.setProgress(75,true);

                    }
                    else{
                        pb.setProgress(25);
                        pb2.setProgress(75);

                    }

                    //pb.setVisibility(View.VISIBLE);


                }
            });


        }
    }




}

package com.smartcitypune.smartpune;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Voting extends AppCompatActivity {
    public static int questionposition = 0;

    public void updateQuesPosition(int position) {
        questionposition = position;
    }

    int buttonposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        final LinearLayout layout = findViewById(R.id.voting_layout);
        final CardView cardView = findViewById(R.id.cardview);
        Intent intent = getIntent();
        //poll ka object
        final Poll poll;
        poll = (Poll) intent.getExtras().get("Voting");

        //question
        TextView question = layout.findViewById(R.id.question);
        question.setText(poll.getQuestion());

        //date
        TextView date = layout.findViewById(R.id.date);
        date.setText(poll.getStartdate() + " - " + poll.getEnddate());

        //firebase initialization
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("data/polls");
        final FirebaseAuth[] mAuth = new FirebaseAuth[1];

        //unique key of question milri(iske andar multipleuid of clients rahenge)
        String str = databaseReference.getKey();

        //arraylist of button progressbar text
        final ArrayList<Button> btnlist = new ArrayList<>();
        final ArrayList<ProgressBar> pblist = new ArrayList<>();
        final ArrayList<TextView> tvlist = new ArrayList<>();

        //total no of responses
        final TextView totalAttempted = new TextView(this);
        totalAttempted.setVisibility(View.GONE);
        int totalresponses = 0;
        for (int c : poll.getCount())
            totalresponses += c;
        totalAttempted.setText("Number of responses : " + totalresponses);
        layout.addView(totalAttempted);

        //0se start hora no of responses--- pb,tv
        //buttonposition count update karne k liye -> button,pb
        buttonposition = 0;

        //for each option ofanswer
        for (final String response : poll.getResponses()) {

            //button for ans
            final Button buttonView = new Button(this);
            buttonView.setTag(buttonposition);

            //text for answer
            final TextView tv = new TextView(this);
            tv.setText(response.toUpperCase());

            //pb for ans
            final ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setTag(buttonposition);
            pb.setScaleY(5);
            pb.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);

            buttonposition++;

            // set some properties of rowTextView or something
            buttonView.setText(response);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 30, 30, 30);
            pb.setLayoutParams(layoutParams);
            tv.setLayoutParams(layoutParams);
            totalAttempted.setLayoutParams(layoutParams);


            // add the textview,pb,bv to the linearlayout
            layout.addView(buttonView);
            layout.addView(tv);
            layout.addView(pb);
            btnlist.add(buttonView);
            pblist.add(pb);
            tvlist.add(tv);

            final int finalTotalresponses = totalresponses;


            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    totalAttempted.setVisibility(View.VISIBLE);

                    int position = 0;

                    int updateCount = 0;

                    for (Button btn : btnlist) {
                        btn.setVisibility(View.GONE);

                    }


                    for (ProgressBar pb : pblist) {
                        int tvposition = pblist.indexOf(pb);
                        pb.setVisibility(View.VISIBLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            int currentprogress = poll.getCount().get(position);
                            tvlist.get(tvposition).setVisibility(View.VISIBLE);
                            float progress = (float) currentprogress / (float) finalTotalresponses * 100;
                            pb.setProgress((int) (progress));
                            if (progress > 75)
                                pb.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            else if (progress < 75 && progress > 30)
                                pb.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            else
                                pb.setProgressTintList(ColorStateList.valueOf(Color.RED));


                        } else {

                            int currentprogress = poll.getCount().get(position);
                            tvlist.get(tvposition).setVisibility(View.VISIBLE);
                            float progress = (float) currentprogress / (float) finalTotalresponses * 100;
                            pb.setProgress((int) (progress));
                            Log.e("questionid", "" + questionposition);
                            Log.e("answerbutton", "" + buttonView.getTag());
                            if (progress > 75)
                                pb.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            else if (progress < 75 && progress > 30)
                                pb.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            else
                                pb.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        position++;
                    }

                    //count updated
                    updateCount = poll.getCount().get((int) buttonView.getTag()) + 1;
                    final int finalUpdateCount = updateCount;

                    //database me update karre value
                    databaseReference.orderByChild("question").equalTo(poll.getQuestion())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot userdataSnapshot : dataSnapshot.getChildren()) {
                                        //question k key
                                        String key = userdataSnapshot.getKey();
                                        //databaseReference.child(key).child("attempted").setValue("yes");
                                        databaseReference.child(key).child("count").child(buttonView.getTag().toString()).setValue(finalUpdateCount);

                                        //pushing key to arraylist
                                        mAuth[0] = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = mAuth[0].getCurrentUser();
                                        String userkey = currentUser.getUid();
                                        poll.getUid().add(userkey);
                                        databaseReference.child(key).child("uid").setValue(poll.getUid());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    Log.e("updatedvalue", "" + updateCount);
                }
            });


        }
    }


}

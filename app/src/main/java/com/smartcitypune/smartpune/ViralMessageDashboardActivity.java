package com.smartcitypune.smartpune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class ViralMessageDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral_message_dashboard);

        CardView viewViralMessages = (CardView) findViewById(R.id.view_viral_messages_button);
        CardView submitViralMessages = (CardView) findViewById(R.id.submit_viral_messages_button);

        viewViralMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViralMessageDashboardActivity.this,ViewViralMessagesActivity.class));
            }
        });

        submitViralMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViralMessageDashboardActivity.this,ReportViralMessage.class));
            }
        });
    }
}

package com.smartcitypune.smartpune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class RoadSafetyDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_safety_dashboard);

        CardView accidentsCardView = (CardView) findViewById(R.id.accidentsCardView);
        CardView potholesCardView = (CardView) findViewById(R.id.potholesCardView);
        CardView miscellaneousCardView = (CardView) findViewById(R.id.miscellaneousCardView);
        CardView crimesCardView = (CardView) findViewById(R.id.crimesCardView);
        CardView reportCardView = (CardView) findViewById(R.id.reportCardView);

        accidentsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoadSafetyDashboardActivity.this, HotspotMapActivity.class));
            }
        });
        reportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoadSafetyDashboardActivity.this, ReportRoadProblemActivity.class));
            }
        });
    }

}

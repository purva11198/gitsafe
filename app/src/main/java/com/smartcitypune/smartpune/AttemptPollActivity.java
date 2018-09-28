package com.smartcitypune.smartpune;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class AttemptPollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_poll);
        ProgressBar progressbar1 = (ProgressBar) findViewById(R.id.progressBar1);
        int color1 = 0xFF43A047;
        progressbar1.getIndeterminateDrawable().setColorFilter(color1, PorterDuff.Mode.SRC_IN);
        progressbar1.getProgressDrawable().setColorFilter(color1, PorterDuff.Mode.SRC_IN);

        ProgressBar progressbar2 = (ProgressBar) findViewById(R.id.progressBar2);
        int color2 = 0xFFe53935;
        progressbar2.getIndeterminateDrawable().setColorFilter(color2, PorterDuff.Mode.SRC_IN);
        progressbar2.getProgressDrawable().setColorFilter(color2, PorterDuff.Mode.SRC_IN);
    }
}

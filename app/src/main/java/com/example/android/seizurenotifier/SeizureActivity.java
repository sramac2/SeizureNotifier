package com.example.android.seizurenotifier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SeizureActivity extends AppCompatActivity {

    private TextView descriptionText;
    private TextView dateText;
    private TextView startTimeText;
    private TextView endTimeText;
    private TextView injuriesText;
    private TextView possibleTriggersText;
    private TextView durationtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure);

        descriptionText = findViewById(R.id.description_seizure);
        injuriesText = findViewById(R.id.injuries_seizure);
        dateText = findViewById(R.id.date_seizure);
        startTimeText = findViewById(R.id.start_time_seizure);
        endTimeText = findViewById(R.id.end_time_seizure);
        possibleTriggersText = findViewById(R.id.possible_triggers_seizure);
        durationtext = findViewById(R.id.duration_seizure);

        Intent intent = getIntent();

        durationtext.setText(intent.getStringExtra("duration"));
        injuriesText.setText(intent.getStringExtra("injuries"));
        dateText.setText(intent.getStringExtra("date"));
        possibleTriggersText.setText(intent.getStringExtra("possibleTriggers"));
        startTimeText.setText(intent.getStringExtra("startTime"));
        endTimeText.setText(intent.getStringExtra("endTime"));
        descriptionText.setText(intent.getStringExtra("description"));
    }
}

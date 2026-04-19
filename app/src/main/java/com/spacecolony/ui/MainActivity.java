package com.spacecolony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.spacecolony.R;
import com.spacecolony.model.Location;
import com.spacecolony.model.Storage;

/**
 * Main screen / Colony Overview.
 * Shows crew counts per location and navigation buttons.
 */
public class MainActivity extends AppCompatActivity {

    private Storage storage;
    private TextView tvColonyName, tvQuartersCount, tvSimulatorCount,
            tvMissionCount, tvMedbayCount, tvTotalCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = Storage.getInstance();

        tvColonyName     = findViewById(R.id.tvColonyName);
        tvQuartersCount  = findViewById(R.id.tvQuartersCount);
        tvSimulatorCount = findViewById(R.id.tvSimulatorCount);
        tvMissionCount   = findViewById(R.id.tvMissionControlCount);
        tvMedbayCount    = findViewById(R.id.tvMedbayCount);
        tvTotalCrew      = findViewById(R.id.tvTotalCrew);

        // Navigation buttons
        findViewById(R.id.btnRecruit).setOnClickListener(v ->
                startActivity(new Intent(this, RecruitActivity.class)));
        findViewById(R.id.btnQuarters).setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));
        findViewById(R.id.btnSimulator).setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));
        findViewById(R.id.btnMissionControl).setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));
        findViewById(R.id.btnStatistics).setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        tvColonyName.setText(storage.getColonyName());
        tvQuartersCount.setText(String.valueOf(storage.getCountByLocation(Location.QUARTERS)));
        tvSimulatorCount.setText(String.valueOf(storage.getCountByLocation(Location.SIMULATOR)));
        tvMissionCount.setText(String.valueOf(storage.getCountByLocation(Location.MISSION_CONTROL)));
        tvMedbayCount.setText(String.valueOf(storage.getCountByLocation(Location.MEDBAY)));
        tvTotalCrew.setText(String.valueOf(storage.getCrewCount()));
    }
}

package com.spacecolony.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.R;
import com.spacecolony.adapter.StatsAdapter;
import com.spacecolony.model.*;
import java.util.List;

/**
 * Statistics screen — shows colony-wide and per-crew stats.
 * Bonus: Statistics feature.
 */
public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Storage storage = Storage.getInstance();

        TextView tvColonyStats = findViewById(R.id.tvColonyStats);
        RecyclerView rvStats   = findViewById(R.id.rvCrewStats);
        rvStats.setLayoutManager(new LinearLayoutManager(this));

        // Colony-wide stats
        String colonyStats = "Colony: " + storage.getColonyName()
                + "\nTotal Crew Recruited: " + storage.getTotalRecruits()
                + "\nTotal Missions: " + storage.getTotalMissions()
                + "\nMissions Launched: " + MissionControl.getMissionCount()
                + "\nActive Crew: " + storage.getCrewCount();
        tvColonyStats.setText(colonyStats);

        // Per-crew stats
        List<CrewMember> allCrew = storage.getAllCrew();
        rvStats.setAdapter(new StatsAdapter(allCrew));
    }
}

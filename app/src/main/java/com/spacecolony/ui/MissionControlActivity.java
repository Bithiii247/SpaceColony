package com.spacecolony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.R;
import com.spacecolony.adapter.CrewAdapter;
import com.spacecolony.model.*;
import java.util.List;

/**
 * Mission Control screen — select two crew members and launch a mission.
 */
public class MissionControlActivity extends AppCompatActivity implements CrewAdapter.OnCrewActionListener {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private MissionControl missionControl;
    private Spinner spinnerMissionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);
        missionControl = new MissionControl();

        recyclerView = findViewById(R.id.rvCrew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinnerMissionType = findViewById(R.id.spinnerMissionType);
        ArrayAdapter<MissionType> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, MissionType.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMissionType.setAdapter(spinnerAdapter);

        Button btnLaunch      = findViewById(R.id.btnLaunchMission);
        Button btnToQuarters  = findViewById(R.id.btnReturnToQuarters);

        btnLaunch.setOnClickListener(v -> launchMission());
        btnToQuarters.setOnClickListener(v -> returnToQuarters());

        loadCrew();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrew();
    }

    private void loadCrew() {
        List<CrewMember> crew = missionControl.getReadyCrew();
        if (adapter == null) {
            adapter = new CrewAdapter(crew, this, true);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(crew);
        }
    }

    private void launchMission() {
        List<CrewMember> selected = adapter.getSelected();
        if (selected.size() < 2) {
            Toast.makeText(this, "Select exactly 2 crew members", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selected.size() > 2) {
            Toast.makeText(this, "Select exactly 2 crew members (deselect extras)", Toast.LENGTH_SHORT).show();
            return;
        }
        MissionType missionType = (MissionType) spinnerMissionType.getSelectedItem();

        Intent intent = new Intent(this, MissionActivity.class);
        intent.putExtra("crewAId", selected.get(0).getId());
        intent.putExtra("crewBId", selected.get(1).getId());
        intent.putExtra("missionType", missionType.name());
        startActivity(intent);
    }

    private void returnToQuarters() {
        List<CrewMember> selected = adapter.getSelected();
        if (selected.isEmpty()) {
            Toast.makeText(this, "No crew selected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) missionControl.returnToQuarters(cm);
        Toast.makeText(this, selected.size() + " crew returned to Quarters", Toast.LENGTH_SHORT).show();
        loadCrew();
    }

    @Override
    public void onCrewAction(CrewMember cm) { /* handled by buttons */ }
}

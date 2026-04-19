package com.spacecolony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.R;
import com.spacecolony.adapter.CrewAdapter;
import com.spacecolony.model.*;
import java.util.List;

/**
 * Quarters screen — shows crew at home.
 * Users can select crew and move them to Simulator or Mission Control.
 */
public class QuartersActivity extends AppCompatActivity implements CrewAdapter.OnCrewActionListener {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Quarters quarters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);
        quarters = new Quarters();

        recyclerView = findViewById(R.id.rvCrew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnToSimulator      = findViewById(R.id.btnToSimulator);
        Button btnToMissionControl = findViewById(R.id.btnToMissionControl);

        btnToSimulator.setOnClickListener(v -> moveSelected(Location.SIMULATOR));
        btnToMissionControl.setOnClickListener(v -> moveSelected(Location.MISSION_CONTROL));

        loadCrew();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrew();
    }

    private void loadCrew() {
        List<CrewMember> crew = quarters.getResidents();
        if (adapter == null) {
            adapter = new CrewAdapter(crew, this, true);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(crew);
        }
    }

    private void moveSelected(Location destination) {
        List<CrewMember> selected = adapter.getSelected();
        if (selected.isEmpty()) {
            Toast.makeText(this, "No crew selected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) {
            if (destination == Location.SIMULATOR) quarters.moveToSimulator(cm);
            else quarters.moveToMissionControl(cm);
        }
        String dest = destination == Location.SIMULATOR ? "Simulator" : "Mission Control";
        Toast.makeText(this, selected.size() + " crew moved to " + dest, Toast.LENGTH_SHORT).show();
        loadCrew();
    }

    @Override
    public void onCrewAction(CrewMember cm) { /* handled by move buttons */ }
}

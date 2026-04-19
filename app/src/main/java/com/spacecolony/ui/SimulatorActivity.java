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
 * Simulator screen — crew members train here to gain experience.
 */
public class SimulatorActivity extends AppCompatActivity implements CrewAdapter.OnCrewActionListener {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Simulator simulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);
        simulator = new Simulator();

        recyclerView = findViewById(R.id.rvCrew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnTrain      = findViewById(R.id.btnTrain);
        Button btnToQuarters = findViewById(R.id.btnToQuarters);

        btnTrain.setOnClickListener(v -> trainSelected());
        btnToQuarters.setOnClickListener(v -> sendToQuarters());

        loadCrew();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrew();
    }

    private void loadCrew() {
        List<CrewMember> crew = simulator.getTrainees();
        if (adapter == null) {
            adapter = new CrewAdapter(crew, this, true);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(crew);
        }
    }

    private void trainSelected() {
        List<CrewMember> selected = adapter.getSelected();
        if (selected.isEmpty()) {
            Toast.makeText(this, "No crew selected", Toast.LENGTH_SHORT).show();
            return;
        }
        simulator.trainAll(selected);
        StringBuilder sb = new StringBuilder("Training complete!\n");
        for (CrewMember cm : selected) {
            sb.append(cm.getName()).append(" — Skill: ").append(cm.getEffectiveSkill())
              .append(", XP: ").append(cm.getExperience()).append("\n");
        }
        Toast.makeText(this, sb.toString().trim(), Toast.LENGTH_LONG).show();
        loadCrew();
    }

    private void sendToQuarters() {
        List<CrewMember> selected = adapter.getSelected();
        if (selected.isEmpty()) {
            Toast.makeText(this, "No crew selected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) simulator.sendToQuarters(cm);
        Toast.makeText(this, selected.size() + " crew returned to Quarters (energy restored)",
                Toast.LENGTH_SHORT).show();
        loadCrew();
    }

    @Override
    public void onCrewAction(CrewMember cm) { /* handled by buttons */ }
}

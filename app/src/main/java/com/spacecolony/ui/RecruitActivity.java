package com.spacecolony.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.spacecolony.R;
import com.spacecolony.model.*;

/**
 * Screen for recruiting new crew members.
 * User enters name and selects specialization via RadioButtons.
 */
public class RecruitActivity extends AppCompatActivity {

    private EditText etName;
    private RadioGroup rgSpecialization;
    private TextView tvStatPreview;
    private Quarters quarters;

    private static final String[] SPECS = {"Pilot","Engineer","Medic","Scientist","Soldier"};
    private static final String[] STAT_PREVIEWS = {
        "Skill: 5  |  Resilience: 4  |  Max Energy: 20\nNavigation Expert: +2 on navigation missions",
        "Skill: 6  |  Resilience: 3  |  Max Energy: 19\nRepair Specialist: +2 on repair missions",
        "Skill: 7  |  Resilience: 2  |  Max Energy: 18\nField Medic: Can heal partner once per mission",
        "Skill: 8  |  Resilience: 1  |  Max Energy: 17\nXenologist: +2 on alien contact missions",
        "Skill: 9  |  Resilience: 0  |  Max Energy: 16\nCombat Specialist: +2 on combat missions"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);
        quarters = new Quarters();

        etName           = findViewById(R.id.etCrewName);
        rgSpecialization = findViewById(R.id.rgSpecialization);
        tvStatPreview    = findViewById(R.id.tvStatPreview);

        // Update stat preview when selection changes
        rgSpecialization.setOnCheckedChangeListener((group, checkedId) -> updatePreview());

        // Default selection
        ((RadioButton) rgSpecialization.getChildAt(0)).setChecked(true);
        updatePreview();

        findViewById(R.id.btnCreate).setOnClickListener(v -> recruitCrew());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void updatePreview() {
        int idx = getSelectedIndex();
        if (idx >= 0) tvStatPreview.setText(STAT_PREVIEWS[idx]);
    }

    private int getSelectedIndex() {
        int checkedId = rgSpecialization.getCheckedRadioButtonId();
        for (int i = 0; i < rgSpecialization.getChildCount(); i++) {
            if (rgSpecialization.getChildAt(i).getId() == checkedId) return i;
        }
        return 0;
    }

    private void recruitCrew() {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter a name");
            return;
        }
        int idx = getSelectedIndex();
        if (idx < 0) {
            Toast.makeText(this, "Please select a specialization", Toast.LENGTH_SHORT).show();
            return;
        }
        String spec = SPECS[idx];
        CrewMember cm = quarters.createCrewMember(name, spec);
        Toast.makeText(this, cm.getName() + " the " + spec + " has joined the colony!",
                Toast.LENGTH_LONG).show();
        etName.setText("");
        ((RadioButton) rgSpecialization.getChildAt(0)).setChecked(true);
    }
}

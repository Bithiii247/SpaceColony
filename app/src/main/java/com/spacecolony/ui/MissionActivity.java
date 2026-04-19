package com.spacecolony.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.spacecolony.R;
import com.spacecolony.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mission screen — displays live mission with tactical combat controls.
 * Bonus: Tactical Combat (player chooses action per turn),
 *        Mission Visualization (energy bars update live),
 *        Randomness (built into Threat.attack).
 */
public class MissionActivity extends AppCompatActivity {

    // UI
    private TextView tvMissionTitle, tvThreatName, tvMissionLog;
    private TextView tvCrewAName, tvCrewBName, tvThreatStatus;
    private ProgressBar pbCrewAEnergy, pbCrewBEnergy, pbThreatEnergy;
    private TextView tvCrewAEnergy, tvCrewBEnergy, tvThreatEnergy;
    private LinearLayout layoutTactical;
    private Button btnAttack, btnDefend, btnHeal;
    private ScrollView scrollLog;
    private Button btnFinish;

    // Model
    private CrewMember crewA, crewB;
    private Threat threat;
    private MissionType missionType;
    private MissionControl missionControl;
    private Storage storage;

    // Mission state
    private final List<String> pendingActions = new ArrayList<>();
    private boolean missionRunning = false;
    private boolean waitingForPlayerInput = false;
    private int currentActorIndex = 0;  // 0=crewA, 1=crewB
    private boolean[] alive = {true, true};
    private int round = 1;
    private final StringBuilder logBuilder = new StringBuilder();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        missionControl = new MissionControl();
        storage = Storage.getInstance();

        // Get intent data
        int crewAId = getIntent().getIntExtra("crewAId", -1);
        int crewBId = getIntent().getIntExtra("crewBId", -1);
        String missionTypeName = getIntent().getStringExtra("missionType");
        missionType = MissionType.valueOf(missionTypeName != null ? missionTypeName : "COMBAT");

        crewA = storage.getCrewMember(crewAId);
        crewB = storage.getCrewMember(crewBId);

        if (crewA == null || crewB == null) {
            Toast.makeText(this, "Error: Crew members not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reset medic heals
        if (crewA instanceof Medic) ((Medic) crewA).resetHeal();
        if (crewB instanceof Medic) ((Medic) crewB).resetHeal();

        // Generate threat
        threat = missionControl.generateThreat(missionType);

        bindViews();
        setupMission();
    }

    private void bindViews() {
        tvMissionTitle  = findViewById(R.id.tvMissionTitle);
        tvThreatName    = findViewById(R.id.tvThreatName);
        tvMissionLog    = findViewById(R.id.tvMissionLog);
        tvCrewAName     = findViewById(R.id.tvCrewAName);
        tvCrewBName     = findViewById(R.id.tvCrewBName);
        tvThreatStatus  = findViewById(R.id.tvThreatStatus);
        pbCrewAEnergy   = findViewById(R.id.pbCrewAEnergy);
        pbCrewBEnergy   = findViewById(R.id.pbCrewBEnergy);
        pbThreatEnergy  = findViewById(R.id.pbThreatEnergy);
        tvCrewAEnergy   = findViewById(R.id.tvCrewAEnergy);
        tvCrewBEnergy   = findViewById(R.id.tvCrewBEnergy);
        tvThreatEnergy  = findViewById(R.id.tvThreatEnergy);
        layoutTactical  = findViewById(R.id.layoutTactical);
        btnAttack       = findViewById(R.id.btnAttack);
        btnDefend       = findViewById(R.id.btnDefend);
        btnHeal         = findViewById(R.id.btnHeal);
        scrollLog       = findViewById(R.id.scrollLog);
        btnFinish       = findViewById(R.id.btnFinish);

        btnAttack.setOnClickListener(v -> playerChooseAction("ATTACK"));
        btnDefend.setOnClickListener(v -> playerChooseAction("DEFEND"));
        btnHeal.setOnClickListener(v -> playerChooseAction("HEAL"));
        btnFinish.setOnClickListener(v -> finish());
    }

    private void setupMission() {
        tvMissionTitle.setText("MISSION: " + missionType.getDisplayName());
        tvThreatName.setText(threat.getName());

        updateAllBars();
        appendLog("=== " + missionType.getDisplayName() + " ===");
        appendLog("Threat: " + threat.toString());
        appendLog("Crew A: " + crewA.toString());
        appendLog("Crew B: " + crewB.toString());
        appendLog("");

        // Show/hide heal button based on crew composition
        boolean hasMedic = (crewA instanceof Medic) || (crewB instanceof Medic);
        btnHeal.setVisibility(hasMedic ? View.VISIBLE : View.GONE);

        missionRunning = true;
        currentActorIndex = 0;
        promptNextAction();
    }

    private void promptNextAction() {
        if (!missionRunning) return;

        // Skip dead crew
        while (currentActorIndex < 2 && !alive[currentActorIndex]) {
            currentActorIndex++;
        }

        if (currentActorIndex >= 2) {
            // Both took their turn this round — start new round
            round++;
            currentActorIndex = 0;
            appendLog("\n--- Round " + round + " ---");
            promptNextAction();
            return;
        }

        CrewMember current = (currentActorIndex == 0) ? crewA : crewB;

        if (threat.isDefeated() || (!alive[0] && !alive[1])) {
            endMission();
            return;
        }

        // Show who is acting
        appendLog("\n" + current.getSpecialization() + "(" + current.getName() + ")'s turn:");
        layoutTactical.setVisibility(View.VISIBLE);

        // Grey out heal if not a medic or already used
        boolean isCurrentMedic = (current instanceof Medic);
        boolean healAvailable  = isCurrentMedic && !((Medic) current).isHealUsed();
        btnHeal.setEnabled(healAvailable);
        btnHeal.setVisibility((crewA instanceof Medic || crewB instanceof Medic) ? View.VISIBLE : View.GONE);

        waitingForPlayerInput = true;
    }

    private void playerChooseAction(String action) {
        if (!waitingForPlayerInput) return;
        waitingForPlayerInput = false;
        layoutTactical.setVisibility(View.GONE);

        CrewMember current = (currentActorIndex == 0) ? crewA : crewB;
        CrewMember partner = (currentActorIndex == 0) ? crewB : crewA;
        boolean partnerAlive = (currentActorIndex == 0) ? alive[1] : alive[0];

        executeAction(current, partner, partnerAlive, action, currentActorIndex);
    }

    private void executeAction(CrewMember current, CrewMember partner,
                               boolean partnerAlive, String action, int idx) {

        if ("DEFEND".equals(action)) {
            appendLog(current.getName() + " DEFENDS (+3 resilience this turn)");
            // Threat attacks with reduced damage
            int raw = threat.getSkill() + (int)(Math.random() * 3);
            int dmg = Math.max(1, raw - current.getResilience() - 3);
            current.setEnergy(Math.max(0, current.getEnergy() - dmg));
            appendLog("  Threat hits for " + dmg + "  |  "
                    + current.getName() + " energy: " + current.getEnergy() + "/" + current.getMaxEnergy());

        } else if ("HEAL".equals(action) && current instanceof Medic && !((Medic)current).isHealUsed()) {
            if (partnerAlive) {
                int before = partner.getEnergy();
                partner.setEnergy(Math.min(partner.getMaxEnergy(), partner.getEnergy() + current.getEffectiveSkill()));
                int healed = partner.getEnergy() - before;
                ((Medic) current).resetHeal(); // mark as used
                appendLog(current.getName() + " HEALS " + partner.getName() + " for " + healed + " energy");
                appendLog("  " + partner.getName() + " energy: " + partner.getEnergy() + "/" + partner.getMaxEnergy());
            }
            // Threat still retaliates
            int dmg = threat.attack(current);
            appendLog("  Threat retaliates: " + current.getName()
                    + " takes " + dmg + "  |  energy: " + current.getEnergy() + "/" + current.getMaxEnergy());

        } else {
            // ATTACK with specialization bonus
            int rawDmg = getSkillWithBonus(current, missionType);
            int dealtToThreat = threat.defend(rawDmg);
            appendLog(current.getName() + " ATTACKS " + threat.getName());
            appendLog("  Damage: " + rawDmg + " - " + threat.getResilience() + " = " + dealtToThreat
                    + "  |  Threat energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());

            if (!threat.isDefeated()) {
                int dmg = threat.attack(current);
                appendLog("  Threat retaliates: " + current.getName()
                        + " takes " + dmg + "  |  energy: " + current.getEnergy() + "/" + current.getMaxEnergy());
            }
        }

        // Check if crew member died
        if (!current.isAlive()) {
            alive[idx] = false;
            current.setLocation(Location.MEDBAY);
            appendLog("  ⚠ " + current.getName() + " has been incapacitated → sent to Medbay");
            animateFlash(idx == 0 ? pbCrewAEnergy : pbCrewBEnergy);
        }

        updateAllBars();

        // Check end conditions
        if (threat.isDefeated() || (!alive[0] && !alive[1])) {
            handler.postDelayed(this::endMission, 600);
            return;
        }

        currentActorIndex++;
        handler.postDelayed(this::promptNextAction, 400);
    }

    private int getSkillWithBonus(CrewMember cm, MissionType mt) {
        if (cm instanceof Pilot)     return ((Pilot)     cm).actWithBonus(mt);
        if (cm instanceof Engineer)  return ((Engineer)  cm).actWithBonus(mt);
        if (cm instanceof Scientist) return ((Scientist) cm).actWithBonus(mt);
        if (cm instanceof Soldier)   return ((Soldier)   cm).actWithBonus(mt);
        return cm.act();
    }

    private void endMission() {
        missionRunning = false;
        layoutTactical.setVisibility(View.GONE);
        btnFinish.setVisibility(View.VISIBLE);

        boolean victory = threat.isDefeated();
        Toast.makeText(this,
                victory ? "MISSION SUCCESS 🚀" : "MISSION FAILED 💀",
                Toast.LENGTH_LONG).show();
        storage.incrementMissions();

        if (victory) {
            appendLog("\n=== MISSION COMPLETE ===");
            appendLog("The " + threat.getName() + " has been neutralized!");
            for (int i = 0; i < 2; i++) {
                CrewMember cm = (i == 0) ? crewA : crewB;
                if (alive[i]) {
                    cm.gainExperience(1);
                    cm.recordMissionCompleted(true);
                    cm.setLocation(Location.MISSION_CONTROL);
                    appendLog(cm.getName() + " gains 1 XP  (total XP: " + cm.getExperience() + ")");
                } else {
                    cm.recordMissionCompleted(false);
                }
            }
            tvThreatStatus.setText("DEFEATED ✓");
            tvThreatStatus.setTextColor(ContextCompat.getColor(this, R.color.medic_green));
        } else {
            appendLog("\n=== MISSION FAILED ===");
            appendLog("All crew members incapacitated. Sent to Medbay.");
            crewA.recordMissionCompleted(false);
            crewB.recordMissionCompleted(false);
            tvThreatStatus.setText("SURVIVED ✗");
            tvThreatStatus.setTextColor(ContextCompat.getColor(this, R.color.soldier_red));
        }

        updateAllBars();
    }

    private void updateAllBars() {
        // Crew A
        tvCrewAName.setText(crewA.getSpecialization() + "\n" + crewA.getName());
        pbCrewAEnergy.setMax(crewA.getMaxEnergy());
        pbCrewAEnergy.setProgress(crewA.getEnergy());
        tvCrewAEnergy.setText(crewA.getEnergy() + "/" + crewA.getMaxEnergy());

        // Crew B
        tvCrewBName.setText(crewB.getSpecialization() + "\n" + crewB.getName());
        pbCrewBEnergy.setMax(crewB.getMaxEnergy());
        pbCrewBEnergy.setProgress(crewB.getEnergy());
        tvCrewBEnergy.setText(crewB.getEnergy() + "/" + crewB.getMaxEnergy());

        // Threat
        pbThreatEnergy.setMax(threat.getMaxEnergy());
        pbThreatEnergy.setProgress(threat.getEnergy());
        tvThreatEnergy.setText(threat.getEnergy() + "/" + threat.getMaxEnergy());
    }

    private void appendLog(String line) {
        logBuilder.append(line).append("\n");
        tvMissionLog.setText(logBuilder.toString());
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    private void animateFlash(ProgressBar pb) {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.soldier_red),
                ContextCompat.getColor(this, android.R.color.transparent));
        anim.setDuration(600);
        anim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

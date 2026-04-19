package com.spacecolony.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.R;
import com.spacecolony.model.CrewMember;
import java.util.List;

/**
 * RecyclerView adapter for displaying per-crew statistics.
 */
public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {

    private final List<CrewMember> crewList;

    public StatsAdapter(List<CrewMember> crewList) {
        this.crewList = crewList;
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_stats, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        holder.bind(crewList.get(position));
    }

    @Override
    public int getItemCount() { return crewList.size(); }

    static class StatsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvName, tvSpec, tvStats, tvMissionStats;
        private final View colorBar;

        StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar       = itemView.findViewById(R.id.ivCrewAvatar);
            tvName         = itemView.findViewById(R.id.tvStatName);
            tvSpec         = itemView.findViewById(R.id.tvStatSpec);
            tvStats        = itemView.findViewById(R.id.tvStatDetails);
            tvMissionStats = itemView.findViewById(R.id.tvMissionStats);
            colorBar       = itemView.findViewById(R.id.viewStatColorBar);
        }

        void bind(CrewMember cm) {
            tvName.setText(cm.getName());
            tvSpec.setText(cm.getSpecialization());
            tvStats.setText("Skill: " + cm.getEffectiveSkill()
                    + "  XP: " + cm.getExperience()
                    + "  Location: " + cm.getLocation().name().replace("_", " "));
            tvMissionStats.setText("Missions: " + cm.getMissionsCompleted()
                    + "  Wins: " + cm.getMissionsWon()
                    + "  Training: " + cm.getTrainingSessions());
            ivAvatar.setImageResource(cm.getAvatarResId());
            int color = ContextCompat.getColor(itemView.getContext(), cm.getColorResId());
            colorBar.setBackgroundColor(color);
        }
    }
}

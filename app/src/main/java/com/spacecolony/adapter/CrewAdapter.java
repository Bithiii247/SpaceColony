package com.spacecolony.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.R;
import com.spacecolony.model.CrewMember;
import java.util.*;

/**
 * RecyclerView adapter for displaying crew members with optional checkboxes.
 * Bonus: RecyclerView feature, Crew Images feature.
 */
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    public interface OnCrewActionListener {
        void onCrewAction(CrewMember cm);
    }

    private List<CrewMember> crewList;
    private final Set<Integer> selectedIds = new HashSet<>();
    private final OnCrewActionListener listener;
    private final boolean selectable;

    public CrewAdapter(List<CrewMember> crewList, OnCrewActionListener listener, boolean selectable) {
        this.crewList  = new ArrayList<>(crewList);
        this.listener  = listener;
        this.selectable = selectable;
    }

    public void updateData(List<CrewMember> newList) {
        this.crewList = new ArrayList<>(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<CrewMember> getSelected() {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewList) {
            if (selectedIds.contains(cm.getId())) result.add(cm);
        }
        return result;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember cm = crewList.get(position);
        holder.bind(cm, selectedIds.contains(cm.getId()), selectable);

        holder.itemView.setOnClickListener(v -> {
            if (selectable) {
                if (selectedIds.contains(cm.getId())) selectedIds.remove(cm.getId());
                else selectedIds.add(cm.getId());
                notifyItemChanged(position);
            }
            if (listener != null) listener.onCrewAction(cm);
        });
    }

    @Override
    public int getItemCount() { return crewList.size(); }

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvName, tvSpec, tvStats, tvLocation;
        private final ProgressBar pbEnergy;
        private final CheckBox cbSelect;
        private final View colorBar;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar   = itemView.findViewById(R.id.ivCrewAvatar);
            tvName     = itemView.findViewById(R.id.tvCrewName);
            tvSpec     = itemView.findViewById(R.id.tvCrewSpec);
            tvStats    = itemView.findViewById(R.id.tvCrewStats);
            tvLocation = itemView.findViewById(R.id.tvCrewLocation);
            pbEnergy   = itemView.findViewById(R.id.pbCrewEnergy);
            cbSelect   = itemView.findViewById(R.id.cbSelect);
            colorBar   = itemView.findViewById(R.id.viewColorBar);
        }

        void bind(CrewMember cm, boolean isSelected, boolean selectable) {
            tvName.setText(cm.getName());
            tvSpec.setText(cm.getSpecialization());
            tvStats.setText("Skill: " + cm.getEffectiveSkill()
                    + "  Res: " + cm.getResilience()
                    + "  XP: " + cm.getExperience());
            tvLocation.setText(cm.getLocation().name().replace("_", " "));
            pbEnergy.setMax(cm.getMaxEnergy());
            pbEnergy.setProgress(cm.getEnergy());

            // Crew image (Bonus: Crew Images)
            ivAvatar.setImageResource(cm.getAvatarResId());

            // Specialization color bar
            int color = ContextCompat.getColor(itemView.getContext(), cm.getColorResId());
            colorBar.setBackgroundColor(color);
            pbEnergy.setProgressTintList(ColorStateList.valueOf(color));

            cbSelect.setVisibility(selectable ? View.VISIBLE : View.GONE);
            cbSelect.setChecked(isSelected);
            cbSelect.setOnClickListener(v -> itemView.performClick());

            // Highlight selected item
            itemView.setAlpha(isSelected ? 1.0f : 0.85f);
            itemView.setElevation(isSelected ? 8f : 2f);
        }
    }
}

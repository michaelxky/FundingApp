package Program.Tech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import Program.Tech.R;
import Program.Tech.showProfileAct;
import java.util.List;
import android.widget.TextView;

public class profileActAdapter extends RecyclerView.Adapter<profileActAdapter.ActivityViewHolder> {

    private List<showProfileAct> activities;

    public profileActAdapter(List<showProfileAct> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        holder.bind(activities.get(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.activity_title);
            description = itemView.findViewById(R.id.activity_description);
        }

        public void bind(showProfileAct activity) {
            title.setText(activity.getTitle());
            if (activity.getDescription()!=null)
            {
                description.setText(activity.getDescription());
            }

        }
    }
}

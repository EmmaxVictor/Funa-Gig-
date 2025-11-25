package application.com.funagig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import application.com.funagig.R;
import application.com.funagig.models.Gig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GigAdapter extends RecyclerView.Adapter<GigAdapter.GigViewHolder> {

    private List<Gig> gigList;
    private Context context;
    private OnGigClickListener listener;

    public interface OnGigClickListener {
        void onGigClick(Gig gig);
    }

    public GigAdapter(Context context) {
        this.context = context;
        this.gigList = new ArrayList<>();
    }

    public void setGigList(List<Gig> gigList) {
        this.gigList = gigList != null ? gigList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnGigClickListener(OnGigClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gig, parent, false);
        return new GigViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GigViewHolder holder, int position) {
        Gig gig = gigList.get(position);
        holder.bind(gig);
    }

    @Override
    public int getItemCount() {
        return gigList.size();
    }

    public Gig getGigAt(int position) {
        return gigList.get(position);
    }

    class GigViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGigTitle;
        private TextView tvGigCategory;
        private TextView tvGigBudget;
        private TextView tvGigDescription;
        private TextView tvGigBadge;
        private TextView tvGigLocation;
        private TextView tvGigApplicants;
        private TextView tvGigPostedDate;
        private TextView tvGigUrgency;
        private Button btnViewDetails;

        public GigViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGigTitle = itemView.findViewById(R.id.tv_gig_title);
            tvGigCategory = itemView.findViewById(R.id.tv_gig_category);
            tvGigBudget = itemView.findViewById(R.id.tv_gig_budget);
            tvGigDescription = itemView.findViewById(R.id.tv_gig_description);
            tvGigBadge = itemView.findViewById(R.id.tv_gig_badge);
            tvGigUrgency = itemView.findViewById(R.id.tv_gig_urgency);
            tvGigLocation = itemView.findViewById(R.id.tv_gig_location);
            tvGigApplicants = itemView.findViewById(R.id.tv_gig_applicants);
            tvGigPostedDate = itemView.findViewById(R.id.tv_gig_posted_date);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onGigClick(gigList.get(getAdapterPosition()));
                }
            });

            if (btnViewDetails != null) {
                btnViewDetails.setOnClickListener(v -> {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onGigClick(gigList.get(getAdapterPosition()));
                    }
                });
            }
        }

        public void bind(Gig gig) {
            tvGigTitle.setText(gig.getTitle() != null ? gig.getTitle() : "Untitled Gig");
            tvGigCategory.setText(gig.getCategory() != null ? gig.getCategory() : "General");
            tvGigBudget.setText(gig.getBudget() != null ? "UGX " + gig.getBudget() : "UGX 0");
            tvGigDescription.setText(gig.getDescription() != null ? gig.getDescription() : "No description");
            if (tvGigBadge != null) {
                String badge = gig.getUrgencyLevel();
                if (badge == null || badge.isEmpty()) {
                    badge = gig.getStatus() != null ? gig.getStatus() : "Flexible";
                }
                tvGigBadge.setText(badge);
            }
            String urgency = gig.getUrgencyLevel();
            tvGigUrgency.setText(urgency != null && !urgency.isEmpty() ? "Urgency: " + urgency : "Urgency: Flexible timeline");
            tvGigLocation.setText(gig.getLocation() != null ? "📍 " + gig.getLocation() : "📍 Location not specified");
            tvGigApplicants.setText(gig.getApplicantsCount() + " applicant" + (gig.getApplicantsCount() != 1 ? "s" : ""));

            // Format posted date
            if (gig.getPostedDate() > 0) {
                long timeDiff = System.currentTimeMillis() - gig.getPostedDate();
                long daysDiff = timeDiff / (1000 * 60 * 60 * 24);
                long hoursDiff = timeDiff / (1000 * 60 * 60);
                long minutesDiff = timeDiff / (1000 * 60);

                if (daysDiff > 0) {
                    tvGigPostedDate.setText("Posted " + daysDiff + " day" + (daysDiff > 1 ? "s" : "") + " ago");
                } else if (hoursDiff > 0) {
                    tvGigPostedDate.setText("Posted " + hoursDiff + " hour" + (hoursDiff > 1 ? "s" : "") + " ago");
                } else if (minutesDiff > 0) {
                    tvGigPostedDate.setText("Posted " + minutesDiff + " minute" + (minutesDiff > 1 ? "s" : "") + " ago");
                } else {
                    tvGigPostedDate.setText("Posted just now");
                }
            } else {
                tvGigPostedDate.setText("Posted recently");
            }
        }
    }
}


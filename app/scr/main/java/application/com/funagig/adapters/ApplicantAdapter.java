package application.com.funagig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import application.com.funagig.R;
import application.com.funagig.models.JobApplication;

import java.util.ArrayList;
import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    private List<JobApplication> applicantList;
    private Context context;

    public ApplicantAdapter(Context context) {
        this.context = context;
        this.applicantList = new ArrayList<>();
    }

    public void setApplicantList(List<JobApplication> applicantList) {
        this.applicantList = applicantList != null ? applicantList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public JobApplication getApplicationAt(int position) {
        return applicantList.get(position);
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        JobApplication application = applicantList.get(position);
        holder.bind(application);
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    class ApplicantViewHolder extends RecyclerView.ViewHolder {
        private TextView tvApplicantName;
        private TextView tvApplicantEmail;
        private TextView tvApplicantPhone;
        private TextView tvCoverLetter;
        private TextView tvProposedBudget;
        private TextView tvAppliedDate;
        private TextView tvStatus;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApplicantName = itemView.findViewById(R.id.tv_applicant_name);
            tvApplicantEmail = itemView.findViewById(R.id.tv_applicant_email);
            tvApplicantPhone = itemView.findViewById(R.id.tv_applicant_phone);
            tvCoverLetter = itemView.findViewById(R.id.tv_cover_letter);
            tvProposedBudget = itemView.findViewById(R.id.tv_proposed_budget);
            tvAppliedDate = itemView.findViewById(R.id.tv_applied_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(JobApplication application) {
            tvApplicantName.setText(application.getApplicantName() != null ? application.getApplicantName() : "Unknown");
            tvApplicantEmail.setText(application.getApplicantEmail() != null ? application.getApplicantEmail() : "No email");
            tvApplicantPhone.setText(application.getApplicantPhone() != null ? "📞 " + application.getApplicantPhone() : "📞 No phone");
            tvCoverLetter.setText(application.getCoverLetter() != null ? application.getCoverLetter() : "No cover letter");
            tvProposedBudget.setText(application.getProposedBudget() != null ? "Proposed: UGX " + application.getProposedBudget() : "No budget proposed");
            
            // Format applied date
            if (application.getAppliedDate() > 0) {
                long timeDiff = System.currentTimeMillis() - application.getAppliedDate();
                long daysDiff = timeDiff / (1000 * 60 * 60 * 24);
                long hoursDiff = timeDiff / (1000 * 60 * 60);
                long minutesDiff = timeDiff / (1000 * 60);

                if (daysDiff > 0) {
                    tvAppliedDate.setText("Applied " + daysDiff + " day" + (daysDiff > 1 ? "s" : "") + " ago");
                } else if (hoursDiff > 0) {
                    tvAppliedDate.setText("Applied " + hoursDiff + " hour" + (hoursDiff > 1 ? "s" : "") + " ago");
                } else if (minutesDiff > 0) {
                    tvAppliedDate.setText("Applied " + minutesDiff + " minute" + (minutesDiff > 1 ? "s" : "") + " ago");
                } else {
                    tvAppliedDate.setText("Applied just now");
                }
            } else {
                tvAppliedDate.setText("Applied recently");
            }
            
            // Status
            String status = application.getStatus() != null ? application.getStatus() : "pending";
            tvStatus.setText("Status: " + status.substring(0, 1).toUpperCase() + status.substring(1));
        }
    }
}


package application.com.funagig.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "job_applications",
    foreignKeys = {
        @ForeignKey(
            entity = Gig.class,
            parentColumns = "id",
            childColumns = "gigId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "applicantId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "gigId"),
        @Index(value = "applicantId")
    }
)
public class JobApplication {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int gigId;
    private String gigIdString; // For Firebase compatibility
    private int applicantId;
    private String applicantUid; // Firebase UID
    private String applicantName;
    private String applicantEmail; // Contact info
    private String applicantPhone; // Contact info
    private String coverLetter;
    private String proposedBudget;
    private String status; // pending, accepted, rejected, completed
    private long appliedDate;
    private long reviewedDate;
    private String reviewNotes;
    private long createdAt;
    private long updatedAt;
    
    public JobApplication() {
    }
    
    public JobApplication(int gigId, int applicantId, String applicantName) {
        this.gigId = gigId;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.appliedDate = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = "pending";
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getGigId() {
        return gigId;
    }
    
    public void setGigId(int gigId) {
        this.gigId = gigId;
    }
    
    public int getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(int applicantId) {
        this.applicantId = applicantId;
    }
    
    public String getGigIdString() {
        return gigIdString;
    }
    
    public void setGigIdString(String gigIdString) {
        this.gigIdString = gigIdString;
    }
    
    public String getApplicantUid() {
        return applicantUid;
    }
    
    public void setApplicantUid(String applicantUid) {
        this.applicantUid = applicantUid;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public String getApplicantEmail() {
        return applicantEmail;
    }
    
    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }
    
    public String getApplicantPhone() {
        return applicantPhone;
    }
    
    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    public String getProposedBudget() {
        return proposedBudget;
    }
    
    public void setProposedBudget(String proposedBudget) {
        this.proposedBudget = proposedBudget;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(long appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public long getReviewedDate() {
        return reviewedDate;
    }
    
    public void setReviewedDate(long reviewedDate) {
        this.reviewedDate = reviewedDate;
    }
    
    public String getReviewNotes() {
        return reviewNotes;
    }
    
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}


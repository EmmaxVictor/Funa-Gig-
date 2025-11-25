package application.com.funagig.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "gigs")
public class Gig implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String gigId;
    private String title;
    private String category;
    private String description;
    private String location;
    private String budget;
    private String budgetType; // hourly, fixed, negotiable
    private String postedBy;
    private String urgencyLevel;
    private String contactMethod;
    private String contactPhone;
    private String scheduleDetails;
    private long postedDate;
    private long deadline;
    private String status; // active, completed, cancelled
    private String requiredSkills;
    private String imageUrl;
    private int applicantsCount;
    private long createdAt;
    private long updatedAt;
    
    public Gig() {
    }
    
    public Gig(String title, String category, String description, String location, String budget) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.budget = budget;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.postedDate = System.currentTimeMillis();
        this.status = "active";
        this.urgencyLevel = "Flexible timeline";
        this.contactMethod = "In-App Chat";
        this.applicantsCount = 0;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getGigId() {
        return gigId;
    }
    
    public void setGigId(String gigId) {
        this.gigId = gigId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getBudget() {
        return budget;
    }
    
    public void setBudget(String budget) {
        this.budget = budget;
    }
    
    public String getBudgetType() {
        return budgetType;
    }
    
    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }
    
    public String getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
    
    public String getContactMethod() {
        return contactMethod;
    }
    
    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getScheduleDetails() {
        return scheduleDetails;
    }
    
    public void setScheduleDetails(String scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }
    
    public String getPostedBy() {
        return postedBy;
    }
    
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    
    public long getPostedDate() {
        return postedDate;
    }
    
    public void setPostedDate(long postedDate) {
        this.postedDate = postedDate;
    }
    
    public long getDeadline() {
        return deadline;
    }
    
    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getApplicantsCount() {
        return applicantsCount;
    }
    
    public void setApplicantsCount(int applicantsCount) {
        this.applicantsCount = applicantsCount;
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


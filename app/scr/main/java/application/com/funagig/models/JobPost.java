package application.com.funagig.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "job_posts")
public class JobPost {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private String salary;
    private String description;
    private String postedDate;
    private String applyUrl;
    private String companyLogo;
    private String source; // LinkedIn, BrighterMonday, etc.
    private boolean isSaved;
    private long createdAt;
    private long updatedAt;
    
    public JobPost() {
    }
    
    public JobPost(String jobTitle, String companyName, String location, String jobType, String salary, String description) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.salary = salary;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isSaved = false;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getJobType() {
        return jobType;
    }
    
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    
    public String getSalary() {
        return salary;
    }
    
    public void setSalary(String salary) {
        this.salary = salary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPostedDate() {
        return postedDate;
    }
    
    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
    
    public String getApplyUrl() {
        return applyUrl;
    }
    
    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }
    
    public String getCompanyLogo() {
        return companyLogo;
    }
    
    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public boolean isSaved() {
        return isSaved;
    }
    
    public void setSaved(boolean saved) {
        isSaved = saved;
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


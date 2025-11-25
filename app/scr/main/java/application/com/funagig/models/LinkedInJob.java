package application.com.funagig.models;

public class LinkedInJob {
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private String salary;
    private String description;
    private String postedDate;
    private String applyUrl;
    private String companyLogo;

    public LinkedInJob() {
        // Default constructor
    }

    public LinkedInJob(String jobTitle, String companyName, String location, String jobType, 
                      String salary, String description, String postedDate, String applyUrl) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.salary = salary;
        this.description = description;
        this.postedDate = postedDate;
        this.applyUrl = applyUrl;
    }

    // Getters and Setters
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
}


package application.com.funagig.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import application.com.funagig.models.JobApplication;
import java.util.List;

@Dao
public interface JobApplicationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertApplication(JobApplication application);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertApplications(List<JobApplication> applications);
    
    @Update
    void updateApplication(JobApplication application);
    
    @Delete
    void deleteApplication(JobApplication application);
    
    @Query("SELECT * FROM job_applications WHERE id = :id LIMIT 1")
    LiveData<JobApplication> getApplicationById(int id);
    
    @Query("SELECT * FROM job_applications WHERE gigId = :gigId")
    LiveData<List<JobApplication>> getApplicationsByGigId(int gigId);
    
    @Query("SELECT * FROM job_applications WHERE applicantId = :applicantId")
    LiveData<List<JobApplication>> getApplicationsByApplicant(int applicantId);
    
    @Query("SELECT * FROM job_applications WHERE gigId = :gigId AND applicantId = :applicantId LIMIT 1")
    LiveData<JobApplication> getApplicationByGigAndApplicant(int gigId, int applicantId);
    
    @Query("SELECT * FROM job_applications WHERE status = :status")
    LiveData<List<JobApplication>> getApplicationsByStatus(String status);
    
    @Query("SELECT * FROM job_applications WHERE gigId = :gigId AND status = :status")
    LiveData<List<JobApplication>> getGigApplicationsByStatus(int gigId, String status);
    
    @Query("SELECT * FROM job_applications ORDER BY appliedDate DESC")
    LiveData<List<JobApplication>> getAllApplications();
    
    @Query("DELETE FROM job_applications WHERE id = :id")
    void deleteApplicationById(int id);
    
    @Query("DELETE FROM job_applications WHERE gigId = :gigId")
    void deleteApplicationsByGigId(int gigId);
    
    @Query("UPDATE job_applications SET status = :status, reviewedDate = :reviewedDate, reviewNotes = :notes, updatedAt = :updatedAt WHERE id = :id")
    void updateApplicationStatus(int id, String status, long reviewedDate, String notes, long updatedAt);
}


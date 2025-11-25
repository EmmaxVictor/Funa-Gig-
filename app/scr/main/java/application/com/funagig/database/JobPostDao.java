package application.com.funagig.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import application.com.funagig.models.JobPost;
import java.util.List;

@Dao
public interface JobPostDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJobPost(JobPost jobPost);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJobPosts(List<JobPost> jobPosts);
    
    @Update
    void updateJobPost(JobPost jobPost);
    
    @Delete
    void deleteJobPost(JobPost jobPost);
    
    @Query("SELECT * FROM job_posts WHERE id = :id LIMIT 1")
    LiveData<JobPost> getJobPostById(int id);
    
    @Query("SELECT * FROM job_posts WHERE jobId = :jobId LIMIT 1")
    LiveData<JobPost> getJobPostByJobId(String jobId);
    
    @Query("SELECT * FROM job_posts WHERE source = :source")
    LiveData<List<JobPost>> getJobPostsBySource(String source);
    
    @Query("SELECT * FROM job_posts WHERE isSaved = 1")
    LiveData<List<JobPost>> getSavedJobPosts();
    
    @Query("SELECT * FROM job_posts ORDER BY createdAt DESC")
    LiveData<List<JobPost>> getAllJobPosts();
    
    @Query("SELECT * FROM job_posts WHERE jobTitle LIKE :searchQuery OR companyName LIKE :searchQuery OR location LIKE :searchQuery")
    LiveData<List<JobPost>> searchJobPosts(String searchQuery);
    
    @Query("UPDATE job_posts SET isSaved = :isSaved, updatedAt = :timestamp WHERE id = :id")
    void updateSaveStatus(int id, boolean isSaved, long timestamp);
    
    @Query("DELETE FROM job_posts WHERE id = :id")
    void deleteJobPostById(int id);
    
    @Query("DELETE FROM job_posts WHERE source = :source")
    void deleteJobPostsBySource(String source);
}


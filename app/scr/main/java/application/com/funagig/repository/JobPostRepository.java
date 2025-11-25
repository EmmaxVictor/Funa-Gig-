package application.com.funagig.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import application.com.funagig.database.FunaGigDatabase;
import application.com.funagig.database.JobPostDao;
import application.com.funagig.models.JobPost;
import java.util.List;

public class JobPostRepository {
    
    private JobPostDao jobPostDao;
    
    public JobPostRepository(Context context) {
        FunaGigDatabase database = FunaGigDatabase.getInstance(context);
        jobPostDao = database.jobPostDao();
    }
    
    public void insertJobPost(JobPost jobPost) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobPostDao.insertJobPost(jobPost);
        });
    }
    
    public void insertJobPosts(List<JobPost> jobPosts) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobPostDao.insertJobPosts(jobPosts);
        });
    }
    
    public void updateJobPost(JobPost jobPost) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobPostDao.updateJobPost(jobPost);
        });
    }
    
    public void deleteJobPost(JobPost jobPost) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobPostDao.deleteJobPost(jobPost);
        });
    }
    
    public LiveData<JobPost> getJobPostById(int id) {
        return jobPostDao.getJobPostById(id);
    }
    
    public LiveData<JobPost> getJobPostByJobId(String jobId) {
        return jobPostDao.getJobPostByJobId(jobId);
    }
    
    public LiveData<List<JobPost>> getJobPostsBySource(String source) {
        return jobPostDao.getJobPostsBySource(source);
    }
    
    public LiveData<List<JobPost>> getSavedJobPosts() {
        return jobPostDao.getSavedJobPosts();
    }
    
    public LiveData<List<JobPost>> getAllJobPosts() {
        return jobPostDao.getAllJobPosts();
    }
    
    public LiveData<List<JobPost>> searchJobPosts(String searchQuery) {
        String query = "%" + searchQuery + "%";
        return jobPostDao.searchJobPosts(query);
    }
    
    public void updateSaveStatus(int id, boolean isSaved) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            long timestamp = System.currentTimeMillis();
            jobPostDao.updateSaveStatus(id, isSaved, timestamp);
        });
    }
}


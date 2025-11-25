package application.com.funagig.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import application.com.funagig.database.FunaGigDatabase;
import application.com.funagig.database.JobApplicationDao;
import application.com.funagig.models.JobApplication;
import java.util.List;

public class JobApplicationRepository {
    
    private JobApplicationDao jobApplicationDao;
    
    public JobApplicationRepository(Context context) {
        FunaGigDatabase database = FunaGigDatabase.getInstance(context);
        jobApplicationDao = database.jobApplicationDao();
    }
    
    public void insertApplication(JobApplication application) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobApplicationDao.insertApplication(application);
        });
    }
    
    public void insertApplications(List<JobApplication> applications) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobApplicationDao.insertApplications(applications);
        });
    }
    
    public void updateApplication(JobApplication application) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobApplicationDao.updateApplication(application);
        });
    }
    
    public void deleteApplication(JobApplication application) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            jobApplicationDao.deleteApplication(application);
        });
    }
    
    public LiveData<JobApplication> getApplicationById(int id) {
        return jobApplicationDao.getApplicationById(id);
    }
    
    public LiveData<List<JobApplication>> getApplicationsByGigId(int gigId) {
        return jobApplicationDao.getApplicationsByGigId(gigId);
    }
    
    public LiveData<List<JobApplication>> getApplicationsByApplicant(int applicantId) {
        return jobApplicationDao.getApplicationsByApplicant(applicantId);
    }
    
    public LiveData<JobApplication> getApplicationByGigAndApplicant(int gigId, int applicantId) {
        return jobApplicationDao.getApplicationByGigAndApplicant(gigId, applicantId);
    }
    
    public LiveData<List<JobApplication>> getApplicationsByStatus(String status) {
        return jobApplicationDao.getApplicationsByStatus(status);
    }
    
    public LiveData<List<JobApplication>> getGigApplicationsByStatus(int gigId, String status) {
        return jobApplicationDao.getGigApplicationsByStatus(gigId, status);
    }
    
    public LiveData<List<JobApplication>> getAllApplications() {
        return jobApplicationDao.getAllApplications();
    }
}


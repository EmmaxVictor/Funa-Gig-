package application.com.funagig.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import application.com.funagig.database.FunaGigDatabase;
import application.com.funagig.database.GigDao;
import application.com.funagig.models.Gig;
import java.util.List;

public class GigRepository {
    
    private GigDao gigDao;
    
    public GigRepository(Context context) {
        FunaGigDatabase database = FunaGigDatabase.getInstance(context);
        gigDao = database.gigDao();
    }
    
    public void insertGig(Gig gig) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            gigDao.insertGig(gig);
        });
    }
    
    public void insertGigs(List<Gig> gigs) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            gigDao.insertGigs(gigs);
        });
    }
    
    public void updateGig(Gig gig) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            gigDao.updateGig(gig);
        });
    }
    
    public void deleteGig(Gig gig) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            gigDao.deleteGig(gig);
        });
    }
    
    public void deleteGigByGigId(String gigId) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            gigDao.deleteGigByGigId(gigId);
        });
    }
    
    public LiveData<Gig> getGigById(int id) {
        return gigDao.getGigById(id);
    }
    
    public LiveData<Gig> getGigByGigId(String gigId) {
        return gigDao.getGigByGigId(gigId);
    }
    
    public LiveData<List<Gig>> getGigsByStatus(String status) {
        return gigDao.getGigsByStatus(status);
    }
    
    public LiveData<List<Gig>> getGigsByUser(String userId) {
        return gigDao.getGigsByUser(userId);
    }
    
    public LiveData<List<Gig>> getGigsByCategory(String category) {
        return gigDao.getGigsByCategory(category);
    }
    
    public LiveData<List<Gig>> getAllGigs() {
        return gigDao.getAllGigs();
    }
    
    public LiveData<List<Gig>> getActiveGigs() {
        return gigDao.getActiveGigs();
    }
    
    public LiveData<List<Gig>> searchGigs(String searchQuery) {
        String query = "%" + searchQuery + "%";
        return gigDao.searchGigs(query);
    }
}


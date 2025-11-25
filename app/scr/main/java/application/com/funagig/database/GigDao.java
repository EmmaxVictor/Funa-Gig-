package application.com.funagig.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import application.com.funagig.models.Gig;
import java.util.List;

@Dao
public interface GigDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGig(Gig gig);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGigs(List<Gig> gigs);
    
    @Update
    void updateGig(Gig gig);
    
    @Delete
    void deleteGig(Gig gig);
    
    @Query("SELECT * FROM gigs WHERE id = :id LIMIT 1")
    LiveData<Gig> getGigById(int id);
    
    @Query("SELECT * FROM gigs WHERE gigId = :gigId LIMIT 1")
    LiveData<Gig> getGigByGigId(String gigId);
    
    @Query("SELECT * FROM gigs WHERE status = :status ORDER BY postedDate DESC")
    LiveData<List<Gig>> getGigsByStatus(String status);
    
    @Query("SELECT * FROM gigs WHERE postedBy = :userId ORDER BY postedDate DESC")
    LiveData<List<Gig>> getGigsByUser(String userId);
    
    @Query("SELECT * FROM gigs WHERE category = :category ORDER BY postedDate DESC")
    LiveData<List<Gig>> getGigsByCategory(String category);
    
    @Query("SELECT * FROM gigs ORDER BY postedDate DESC")
    LiveData<List<Gig>> getAllGigs();
    
    @Query("SELECT * FROM gigs WHERE status = 'active' ORDER BY postedDate DESC")
    LiveData<List<Gig>> getActiveGigs();
    
    @Query("SELECT * FROM gigs WHERE title LIKE :searchQuery OR description LIKE :searchQuery OR category LIKE :searchQuery")
    LiveData<List<Gig>> searchGigs(String searchQuery);
    
    @Query("DELETE FROM gigs WHERE id = :id")
    void deleteGigById(int id);
    
    @Query("DELETE FROM gigs WHERE gigId = :gigId")
    void deleteGigByGigId(String gigId);
    
    @Query("UPDATE gigs SET status = :status, updatedAt = :timestamp WHERE id = :id")
    void updateGigStatus(int id, String status, long timestamp);
    
    @Query("UPDATE gigs SET applicantsCount = applicantsCount + 1 WHERE id = :id")
    void incrementApplicantsCount(int id);
}


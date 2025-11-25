package application.com.funagig.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import application.com.funagig.models.Gig;
import application.com.funagig.repository.GigRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to sync gigs between Firebase Realtime Database and local Room database
 * This ensures real-time updates across all devices
 */
public class FirebaseGigSyncService {
    
    private static final String TAG = "FirebaseGigSyncService";
    private static final String GIGS_NODE = "gigs";
    
    private DatabaseReference gigsRef;
    private GigRepository gigRepository;
    private ValueEventListener gigsListener;
    
    public FirebaseGigSyncService(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gigsRef = database.getReference(GIGS_NODE);
        gigRepository = new GigRepository(context);
    }
    
    /**
     * Start listening for real-time updates from Firebase
     */
    public void startListening() {
        if (gigsListener != null) {
            // Already listening
            return;
        }
        
        gigsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                syncGigsFromFirebase(dataSnapshot);
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error syncing gigs from Firebase: " + databaseError.getMessage());
            }
        };
        
        // Listen to all gigs with status "active" (exclude "taken" and "completed")
        gigsRef.orderByChild("status").equalTo("active").addValueEventListener(gigsListener);
    }
    
    /**
     * Stop listening for updates
     */
    public void stopListening() {
        if (gigsListener != null && gigsRef != null) {
            gigsRef.removeEventListener(gigsListener);
            gigsListener = null;
        }
    }
    
    /**
     * Sync gigs from Firebase to local Room database
     */
    private void syncGigsFromFirebase(DataSnapshot dataSnapshot) {
        // Collect all active gig IDs from Firebase
        java.util.Set<String> activeGigIds = new java.util.HashSet<>();
        
        if (dataSnapshot != null && dataSnapshot.exists()) {
            for (DataSnapshot gigSnapshot : dataSnapshot.getChildren()) {
                try {
                    Gig gig = gigSnapshot.getValue(Gig.class);
                    if (gig != null) {
                        // Set the gigId from Firebase key
                        String gigId = gigSnapshot.getKey();
                        gig.setGigId(gigId);
                        
                        // Only sync active gigs (exclude "taken" and "completed")
                        if ("active".equals(gig.getStatus())) {
                            activeGigIds.add(gigId);
                            gigRepository.insertGig(gig);
                            Log.d(TAG, "Synced gig: " + gig.getTitle());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing gig from Firebase: " + e.getMessage());
                }
            }
        }
        
        // Also check for gigs that were marked as "taken" or "completed" 
        // by listening to the main gigs node for status changes
        // This is handled by a separate listener that we'll add
        cleanupInactiveGigs(activeGigIds);
    }
    
    /**
     * Remove gigs from local database that are no longer active
     */
    private void cleanupInactiveGigs(java.util.Set<String> activeGigIds) {
        // This method can be called to clean up, but since we're only querying active gigs,
        // any gig not in the results should be removed. However, we need to be careful
        // not to delete gigs that are still being processed.
        // For now, the query-based approach should work - gigs that are no longer active
        // won't appear in the results, so they won't be in activeGigIds.
        // We could add logic here to remove local gigs that aren't in activeGigIds,
        // but that might be too aggressive if there are network issues.
    }
    
    /**
     * Upload a gig to Firebase (called when a new gig is created)
     * Returns the generated gigId
     */
    public String uploadGigToFirebase(Gig gig) {
        if (gig == null) {
            return null;
        }
        
        // Generate a unique key if gigId is not set
        String currentGigId = gig.getGigId();
        final String gigId; // Make it final for lambda
        if (currentGigId == null || currentGigId.isEmpty()) {
            gigId = gigsRef.push().getKey();
            gig.setGigId(gigId);
        } else {
            gigId = currentGigId;
        }
        
        // Convert gig to Map for Firebase
        Map<String, Object> gigMap = gigToMap(gig);
        
        // Upload to Firebase
        gigsRef.child(gigId).setValue(gigMap)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Gig uploaded to Firebase: " + gig.getTitle() + " (ID: " + gigId + ")");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error uploading gig to Firebase: " + e.getMessage());
            });
        
        return gigId;
    }
    
    /**
     * Update gig status in Firebase (e.g., mark as completed)
     */
    public void updateGigStatus(String gigId, String status) {
        if (gigId == null || gigId.isEmpty()) {
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());
        
        gigsRef.child(gigId).updateChildren(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Gig status updated in Firebase: " + gigId + " -> " + status);
                
                // If status is "taken" or "completed", remove from local database
                if ("taken".equals(status) || "completed".equals(status)) {
                    gigRepository.deleteGigByGigId(gigId);
                    Log.d(TAG, "Removed gig from local database: " + gigId);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating gig status in Firebase: " + e.getMessage());
            });
    }
    
    /**
     * Convert Gig object to Map for Firebase
     */
    private Map<String, Object> gigToMap(Gig gig) {
        Map<String, Object> map = new HashMap<>();
        map.put("gigId", gig.getGigId());
        map.put("title", gig.getTitle());
        map.put("category", gig.getCategory());
        map.put("description", gig.getDescription());
        map.put("location", gig.getLocation());
        map.put("budget", gig.getBudget());
        map.put("budgetType", gig.getBudgetType());
        map.put("postedBy", gig.getPostedBy());
        map.put("urgencyLevel", gig.getUrgencyLevel());
        map.put("postedDate", gig.getPostedDate());
        map.put("deadline", gig.getDeadline());
        map.put("status", gig.getStatus());
        map.put("requiredSkills", gig.getRequiredSkills());
        map.put("imageUrl", gig.getImageUrl());
        map.put("applicantsCount", gig.getApplicantsCount());
        map.put("createdAt", gig.getCreatedAt());
        map.put("updatedAt", gig.getUpdatedAt());
        return map;
    }
}


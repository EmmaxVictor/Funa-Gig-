package application.com.funagig.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import application.com.funagig.models.JobApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to sync job applications with Firebase Realtime Database
 */
public class FirebaseApplicationSyncService {
    
    private static final String TAG = "FirebaseAppSyncService";
    private static final String APPLICATIONS_NODE = "applications";
    
    private DatabaseReference applicationsRef;
    
    public FirebaseApplicationSyncService(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        applicationsRef = database.getReference(APPLICATIONS_NODE);
    }
    
    /**
     * Submit an application for a gig to Firebase
     */
    public String submitApplication(JobApplication application) {
        if (application == null || application.getGigIdString() == null || application.getGigIdString().isEmpty()) {
            Log.e(TAG, "Cannot submit application: missing gigId");
            return null;
        }
        
        // Generate a unique key for the application
        String applicationId = applicationsRef.child(application.getGigIdString()).push().getKey();
        if (applicationId == null) {
            return null;
        }
        
        // Convert application to Map for Firebase
        Map<String, Object> applicationMap = applicationToMap(application);
        
        // Upload to Firebase under the gig's applications
        applicationsRef.child(application.getGigIdString()).child(applicationId).setValue(applicationMap)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Application submitted to Firebase: " + applicationId);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error submitting application to Firebase: " + e.getMessage());
            });
        
        return applicationId;
    }
    
    /**
     * Update application status in Firebase
     */
    public void updateApplicationStatus(String gigId, String applicationId, String status) {
        if (gigId == null || gigId.isEmpty() || applicationId == null || applicationId.isEmpty()) {
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("reviewedDate", System.currentTimeMillis());
        updates.put("updatedAt", System.currentTimeMillis());
        
        applicationsRef.child(gigId).child(applicationId).updateChildren(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Application status updated in Firebase: " + applicationId + " -> " + status);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating application status in Firebase: " + e.getMessage());
            });
    }
    
    /**
     * Convert JobApplication object to Map for Firebase
     */
    private Map<String, Object> applicationToMap(JobApplication application) {
        Map<String, Object> map = new HashMap<>();
        map.put("gigIdString", application.getGigIdString());
        map.put("applicantUid", application.getApplicantUid());
        map.put("applicantName", application.getApplicantName());
        map.put("applicantEmail", application.getApplicantEmail());
        map.put("applicantPhone", application.getApplicantPhone());
        map.put("coverLetter", application.getCoverLetter());
        map.put("proposedBudget", application.getProposedBudget());
        map.put("status", application.getStatus());
        map.put("appliedDate", application.getAppliedDate());
        map.put("reviewedDate", application.getReviewedDate());
        map.put("reviewNotes", application.getReviewNotes());
        map.put("createdAt", application.getCreatedAt());
        map.put("updatedAt", application.getUpdatedAt());
        return map;
    }
}


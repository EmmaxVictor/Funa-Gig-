package application.com.funagig.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import application.com.funagig.models.Gig;
import application.com.funagig.models.JobApplication;
import application.com.funagig.models.JobPost;
import application.com.funagig.models.User;

@Database(
    entities = {
        User.class,
        Gig.class,
        JobApplication.class,
        JobPost.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class FunaGigDatabase extends RoomDatabase {
    
    private static FunaGigDatabase INSTANCE;
    private static final String DATABASE_NAME = "funa_gig_database";
    
    public abstract UserDao userDao();
    public abstract GigDao gigDao();
    public abstract JobApplicationDao jobApplicationDao();
    public abstract JobPostDao jobPostDao();
    
    public static synchronized FunaGigDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                FunaGigDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // For development only - handles schema changes by recreating the database
            .allowMainThreadQueries() // For development - should be removed in production
            .build();
        }
        return INSTANCE;
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}


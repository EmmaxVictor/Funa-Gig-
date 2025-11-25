package application.com.funagig.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import application.com.funagig.models.User;
import java.util.List;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    LiveData<User> getUserByUid(String uid);
    
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserByUidSync(String uid);
    
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> getUserById(int id);
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    LiveData<User> getUserByEmail(String email);
    
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
    
    @Query("DELETE FROM users WHERE uid = :uid")
    void deleteUserByUid(String uid);
    
    @Query("UPDATE users SET updatedAt = :timestamp WHERE uid = :uid")
    void updateTimestamp(String uid, long timestamp);
}


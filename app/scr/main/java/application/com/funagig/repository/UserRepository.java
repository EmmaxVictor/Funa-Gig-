package application.com.funagig.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import application.com.funagig.database.FunaGigDatabase;
import application.com.funagig.database.UserDao;
import application.com.funagig.models.User;

public class UserRepository {
    
    private UserDao userDao;
    
    public UserRepository(Context context) {
        FunaGigDatabase database = FunaGigDatabase.getInstance(context);
        userDao = database.userDao();
    }
    
    public void insertUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            userDao.insertUser(user);
        });
    }
    
    public void updateUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            userDao.updateUser(user);
        });
    }
    
    public void deleteUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            userDao.deleteUser(user);
        });
    }
    
    public LiveData<User> getUserByUid(String uid) {
        return userDao.getUserByUid(uid);
    }
    
    public User getUserByUidSync(String uid) {
        return userDao.getUserByUidSync(uid);
    }
    
    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }
    
    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    public LiveData<java.util.List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }
}


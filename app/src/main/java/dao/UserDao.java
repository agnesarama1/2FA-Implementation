package dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import model.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users1 WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
}

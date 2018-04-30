package ph.pey.finalproject.sql;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MatchEntityDao {

    @Query("SELECT * FROM MatchEntity")
    List<MatchEntity> getAll();

    @Insert
    void insertAll(MatchEntity... entities);
}

/**
 * By Bertrand NANCY and Kevin NUNES
 */

package ph.pey.finalproject.sql;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {MatchEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MatchEntityDao matchEntityDao();
}

/**
 * By Bertrand NANCY and Kevin NUNES
 * Copyright 2018
 */

package ph.pey.finalproject.sql;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {MatchEntity.class}, version = 2)
@TypeConverters({MatchEntity.ArrayTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MatchEntityDao matchEntityDao();
}

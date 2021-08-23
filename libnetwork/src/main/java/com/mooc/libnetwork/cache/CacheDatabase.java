package com.mooc.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mooc.libcommon.global.AppGlobals;


@Database(entities = {Cache.class},version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    //单例模式
    private static final CacheDatabase database;

    static {
        database= Room.databaseBuilder(AppGlobals.getApplication(),CacheDatabase.class,"ppjoke_cache")
                .allowMainThreadQueries()
                .build();
    }

    //只需要提供相应的抽象方法即可，具体的的方法实现由Room数据库底层实现
    public abstract CacheDao getCache();

    public static CacheDatabase get(){
        return database;
    }
}

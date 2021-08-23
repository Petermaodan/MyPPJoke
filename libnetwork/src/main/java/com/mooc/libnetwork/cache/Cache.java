package com.mooc.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Update;

import java.io.Serializable;

@Entity(tableName = "cache")
public class Cache implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;

    public byte[] data;



}

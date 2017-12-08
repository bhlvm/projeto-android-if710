package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Leonardo on 08/12/2017.
 */
@Database(entities = {ItemFeed.class}, version = 1)
public abstract class ItemFeedDatabase extends RoomDatabase {



    public abstract ItemFeedDAO itemDao();




}

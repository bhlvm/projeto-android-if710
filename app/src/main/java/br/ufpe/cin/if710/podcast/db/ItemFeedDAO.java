package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Leonardo on 08/12/2017.
 */
@Dao
public abstract class ItemFeedDAO {

    @Query("SELECT * FROM itens")
    public abstract List<ItemFeed> getItens();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertItem(ItemFeed item);

    @Update()
    public abstract void update(ItemFeed item);

    @Query("SELECT * FROM itens WHERE downloadLink = :link")
    public abstract ItemFeed getByDownloadLink(String link);

}

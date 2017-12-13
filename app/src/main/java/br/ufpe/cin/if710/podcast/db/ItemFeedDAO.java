package br.ufpe.cin.if710.podcast.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Leonardo on 08/12/2017.
 */
@Dao
public interface  ItemFeedDAO {

    @Query("SELECT * FROM itens")
    public LiveData<List<ItemFeed>> getItens();

    @Insert(onConflict = IGNORE)
    public abstract void insertItem(ItemFeed item);

    @Update()
    public abstract void update(ItemFeed item);

    @Query("SELECT * FROM itens WHERE downloadLink = :link")
    public abstract ItemFeed getByDownloadLink(String link);

    @Delete()
    public abstract void delete(ItemFeed item);

}

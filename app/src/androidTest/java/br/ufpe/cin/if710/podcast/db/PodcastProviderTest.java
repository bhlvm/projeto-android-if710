package br.ufpe.cin.if710.podcast.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Leonardo on 06/12/2017.
 */
@RunWith(AndroidJUnit4.class)
public class PodcastProviderTest extends ProviderTestCase2<PodcastProvider>{

    private MockContentResolver mMockResolver;

    public PodcastProviderTest() {
        super(PodcastProvider.class, "br.ufpe.cin.if710.podcast.feed");
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        ContentValues cv = new ContentValues();
        cv.put(PodcastDBHelper.EPISODE_TITLE, "Podcast setUp");
        cv.put(PodcastDBHelper.EPISODE_LINK, "Podcast setUp");
        cv.put(PodcastDBHelper.EPISODE_DATE, "05/12/2017");
        cv.put(PodcastDBHelper.EPISODE_DESC, "Descrição do Podcast 1");
        cv.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, "www.link_do_podcast_setUp.com.br");
        cv.put(PodcastDBHelper.EPISODE_FILE_URI,"");

        mMockResolver = getMockContentResolver();
        mMockResolver.insert(PodcastProviderContract.EPISODE_LIST_URI,cv);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        mMockResolver = getMockContentResolver();
        mMockResolver.delete(PodcastProviderContract.EPISODE_LIST_URI, "1", null);
    }

    @Test
    public void insert() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put(PodcastDBHelper.EPISODE_TITLE, "Podcast 1");
        cv.put(PodcastDBHelper.EPISODE_LINK, "Podcast 1");
        cv.put(PodcastDBHelper.EPISODE_DATE, "05/12/2017");
        cv.put(PodcastDBHelper.EPISODE_DESC, "Descrição do Podcast 1");
        cv.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, "www.link_do_podcast_1.com.br");
        cv.put(PodcastDBHelper.EPISODE_FILE_URI,"");

        mMockResolver = getMockContentResolver();
        Uri retorno = mMockResolver.insert(PodcastProviderContract.EPISODE_LIST_URI,cv);

        assertNotNull(retorno);
    }

    @Test
    public void delete() throws Exception {
        String where = PodcastDBHelper.EPISODE_DOWNLOAD_LINK + " LIKE ?";
        String[] whereArgs = new String[] { "www.link_do_podcast_setUp.com.br" };

        mMockResolver = getMockContentResolver();
        int rows = mMockResolver.delete(PodcastProviderContract.EPISODE_LIST_URI, where, whereArgs);

        Assert.assertEquals(1, rows);
    }

    @Test
    public void query() throws Exception {
        String[] projection = new String[] { PodcastDBHelper.EPISODE_DOWNLOAD_LINK };

        mMockResolver = getMockContentResolver();
        Cursor cursor = mMockResolver.query(
               PodcastProviderContract.EPISODE_LIST_URI,
               projection,
                null,
                null,
               null
               );

        int count = cursor.getCount();
        assertEquals(1, count);
    }
}
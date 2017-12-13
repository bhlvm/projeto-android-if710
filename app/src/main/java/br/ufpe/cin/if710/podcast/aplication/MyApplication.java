package br.ufpe.cin.if710.podcast.aplication;

import android.app.Application;
import android.arch.persistence.room.Room;


import com.squareup.leakcanary.LeakCanary;

import br.ufpe.cin.if710.podcast.BuildConfig;
import br.ufpe.cin.if710.podcast.db.ItemFeedDatabase;
import br.ufpe.cin.if710.podcast.service.MusicPlayer;
import br.ufpe.cin.if710.podcast.ui.ItemFeedListViewModel;

/**
 * Created by Leonardo on 09/10/2017.
 */

public class MyApplication extends Application {

    //Monitora se a aplicação está em primeiro Plano

    private static boolean isInForeground;
    private static  boolean bound;
    private static MusicPlayer musicPlayer;
    public static ItemFeedListViewModel viewModel;

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        
        //MyApplication.database =  Room.databaseBuilder(this, ItemFeedDatabase.class, "we-need-db").build();
        LeakCanary.install(this);
        // Normal app init code...
    }


    public static boolean isActivityVisible() {
        return isInForeground;
    }

    public static void activityResumed() {
        isInForeground = true;
    }

    public static void activityPaused() {
        isInForeground = false;
    }

    public static void setBound(boolean x) {
        bound = x;
    }

    public static MusicPlayer getMusicPlayer() {return  musicPlayer;}

    public static void setMusicPlayer(MusicPlayer m) { musicPlayer = m;}

    public static void activityStop() {
        bound = false;
    }

    public static boolean isBound() {return bound;}

}

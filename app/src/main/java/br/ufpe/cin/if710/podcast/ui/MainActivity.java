package br.ufpe.cin.if710.podcast.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.aplication.MyApplication;
import br.ufpe.cin.if710.podcast.db.PodcastDBHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.service.DownloadService;
import br.ufpe.cin.if710.podcast.service.MusicPlayer;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    private static String FEED_LAST_MODIFIED = "LAST_MODIFIED";
    //TODO teste com outros links de podcast
    PodcastDBHelper db;
    private ListView itens;


    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = PodcastDBHelper.getInstance(this);
        itens = (ListView) findViewById(R.id.items);
        checkPermissions(this);
        new startServiceMusicTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new DownloadXmlTask().execute(RSS_FEED);


    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) itens.getAdapter();
        adapter.clear();  //muda o estado da myaplication
    }

    protected void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter(DownloadService.DOWNLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadCompleteEvent, f);
        MyApplication.activityResumed(); //muda o estado da myaplication
    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onDownloadCompleteEvent);
        MyApplication.activityPaused();

        super.onPause();
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }

    private ServiceConnection sConn = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyApplication.setMusicPlayer(null);
            MyApplication.setBound(false);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyApplication.setMusicPlayer(((MusicPlayer.MusicBinder) service).getService());
            MyApplication.setBound(true);
        }
    };

    private BroadcastReceiver onDownloadCompleteEvent=new BroadcastReceiver() {
        public void onReceive(Context context, Intent i) {

            Toast.makeText(context, "Download Concluido", Toast.LENGTH_LONG).show();

            new OnDownloadCompleteTask().execute(RSS_FEED);
        }
    };


    public static void checkPermissions(Activity activity) {

        int permissao = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissao != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,PERMISSIONS, 1);
        }
    }

    private class OnDownloadCompleteTask extends AsyncTask<String, Void, List<ItemFeed>>{

        @Override
        protected  List<ItemFeed> doInBackground(String... strings) {
            List<ItemFeed> itemList = MyApplication.database.itemDao().getItens();

            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, itemList);
            System.out.println("Primeiro plano");


            for (ItemFeed i: itemList) {
                Log.d(null, i.getFileUri());
            }

            return itemList;
        }


        @Override
        protected void onPostExecute(List<ItemFeed> feed) {

            //Adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            //atualizar o list view
            itens.setAdapter(adapter);
            itens.setTextFilterEnabled(true);
            // t = new LoadCursorTask().execute();

        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();

            Toast.makeText(getApplicationContext(), "LOL...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList;

            ConnectivityManager cm =  (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            //VERIFICA SE TEM INTERNET
            if( isConnected){
                try {
                    boolean b = ifModified(params[0]);
                    if(b) {
                        itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                        persistirDados(itemList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            //BUSCA OS DADOS PERSISTIDOS
            itemList = MyApplication.database.itemDao().getItens();

            return itemList;
        }



        protected   void persistirDados (List<ItemFeed> itemList){

            List<ItemFeed> itensSalvos = MyApplication.database.itemDao().getItens();
            //ADICIONA SOMENTE ITENS QUE NÃO FORAM PREVIAMENTE CADASTRADOS
            for (ItemFeed item : itemList) {

                if(!itensSalvos.contains(item)){
                    itensSalvos.add(item);

                    MyApplication.database.itemDao().insertItem(item);
                }
            }
        }


        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            //atualizar o list view
            itens.setAdapter(adapter);
            itens.setTextFilterEnabled(true);
        }
    }



    private class startServiceMusicTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!MyApplication.isBound()) {

                Intent bindIntent = new Intent(getApplicationContext(),MusicPlayer.class);
                MyApplication.setBound(bindService(bindIntent, sConn, Context.BIND_AUTO_CREATE));
            }

            return null;
        }
    }

     //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed;
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    private boolean ifModified(String feed) {
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            SharedPreferences prefs = getPreferences(0);
            String last_modified = prefs.getString(FEED_LAST_MODIFIED, "");

            if (!last_modified.isEmpty()) {
                conn.setRequestProperty("If-Modified-Since", last_modified);
                if (conn.getResponseCode() == 304) { // NOT MODIFIED
                    return false;
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(FEED_LAST_MODIFIED, conn.getHeaderField("Last-Modified"));
                    editor.commit();
                }
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(FEED_LAST_MODIFIED, conn.getHeaderField("Last-Modified"));
                editor.commit();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
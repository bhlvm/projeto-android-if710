package br.ufpe.cin.if710.podcast.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(tableName = "itens")
public class ItemFeed {

    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "link")
    private final String link;
    @ColumnInfo(name = "pubDate")
    private final String pubDate;
    @ColumnInfo(name = "description")
    private final String description;

    @PrimaryKey @NonNull
    @ColumnInfo(name = "downloadLink")
    private final String downloadLink;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "fileUri")
    private  String fileUri;


    public ItemFeed(String title, String link, String pubDate, String description, String downloadLink, String fileUri) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
        this.fileUri = fileUri;
        this.status = "baixar";
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getFileUri() {return fileUri;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;};

    public void setFileUri(String fileUri) {this.fileUri = fileUri;};

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((ItemFeed) obj).title);
    }
}
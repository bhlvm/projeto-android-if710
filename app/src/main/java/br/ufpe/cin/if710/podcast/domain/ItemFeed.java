package br.ufpe.cin.if710.podcast.domain;

import java.util.Objects;

public class ItemFeed {
    private final String title;
    private final String link;
    private final String pubDate;
    private final String description;
    private final String downloadLink;
    private String stauts;
    private  String fileUri;


    public ItemFeed(String title, String link, String pubDate, String description, String downloadLink, String fileUri) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
        this.fileUri = fileUri;
        this.stauts = "baixar";
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

    public String getStatus() {return stauts;}

    public void setStauts(String status) {this.stauts = status;};

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((ItemFeed) obj).title);
    }
}
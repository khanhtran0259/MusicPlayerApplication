package com.example.musicplayerapp;

import java.time.Duration;

public class FavouriteSongs {

    private  String title;
    private  String artist;
    private  String duration;
    private  String img;
    private  String song;


    public FavouriteSongs(String title, String artist, String duration, String img, String song) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.img = img;
        this.song = song;
    }


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getImg() {
        return img;
    }

    public String getSong() {
        return song;
    }
}

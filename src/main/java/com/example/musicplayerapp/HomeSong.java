package com.example.musicplayerapp;



public class HomeSong {
    private final  int number;
    private final String title;
    private final String artist;
    private final String duration;

    private final String img;
    private final String song;



    public HomeSong(int number, String title, String artist, String duration, String img, String song) {
        this.number = number;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.img = img;
        this.song = song;
    }





    public int getNumber() {
        return number;
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

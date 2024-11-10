package com.example.musicplayerapp;

import com.example.musicplayerapp.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Label;



public class SongController {


    @FXML
    private Label songName;

    @FXML
    private Label artist;

    public void setData(Song song){


        songName.setText(song.getName());
        artist.setText(song.getArtist());
    }
}
